pipeline {
    agent any
    
    environment {
        SONARQUBE_URL = 'http://localhost:9000'  
        SONARQUBE_TOKEN = credentials('sonarqube-token')
        NEXUS_URL = 'http://localhost:8081/repository/maven-releases/'
        DOCKERHUB_CREDENTIALS = credentials('dockerhub-credentials') 
    }
   
    stages {
        stage('Checkout SCM') {
            steps {
                checkout scm
            }
        }

        stage('Clean and Build') {
            steps {
                script {
                    sh 'mvn clean install'
                }
            }
        }

        stage('Tests Unitaires') {
            steps {
                script {
                    sh 'mvn test'  
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Code Coverage (JaCoCo)') {
            steps {
                sh 'mvn verify' 
                jacoco execPattern: '**/target/jacoco.exec',
                       classPattern: '**/target/classes',
                       sourcePattern: '**/src/main/java',
                       inclusionPattern: '**/*.class',
                       exclusionPattern: '**/*Test*'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                script {
                    withCredentials([string(credentialsId: 'sonarqube-token', variable: 'SONAR_TOKEN')]) {
                        sh """
                            mvn sonar:sonar \
                            -Dsonar.projectKey=FoyerApp \
                            -Dsonar.host.url=${SONARQUBE_URL} \
                            -Dsonar.login=${SONAR_TOKEN} \
                            -Dsonar.java.binaries=target/classes \
                            -Dsonar.sourceEncoding=UTF-8
                        """
                    }
                }
            }
        }

        stage('Deploy to Nexus') {
            steps {
                script {
                    // contenui du dossier
                    sh 'ls -la'
                    
                    // création settings.xml
                    withCredentials([usernamePassword(
                        credentialsId: 'nexus-credentials',
                        usernameVariable: 'NEXUS_USER',
                        passwordVariable: 'NEXUS_PASS'
                    )]) {
                        sh '''
                            echo "Creating settings.xml with Nexus credentials"
                            cat > settings.xml <<EOF
                            <settings>
                              <servers>
                                <server>
                                  <id>nexus-releases</id>
                                  <username>${NEXUS_USER}</username>
                                  <password>${NEXUS_PASS}</password>
                                </server>
                              </servers>
                            </settings>
                            EOF
                            
                            echo "Contents of settings.xml:"
                            cat settings.xml
                            
                            echo "Attempting deployment to Nexus"
                            mvn deploy \
                              -DaltDeploymentRepository=nexus-releases::default::${NEXUS_URL} \
                              -DrepositoryId=nexus-releases \
                              -s settings.xml
                        '''
                    }
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    def imageName = "saraiguess/foyer-app:${env.BUILD_NUMBER}"
                    
                    sh "docker build -t ${imageName} ."
                    
                    env.DOCKER_IMAGE_NAME = imageName
                }
            }
        }

        stage('Push to Docker Hub') {
            steps {
                script {
                    withCredentials([usernamePassword(
                        credentialsId: 'dockerhub-credentials',
                        usernameVariable: 'DOCKERHUB_USER',
                        passwordVariable: 'DOCKERHUB_PASS'
                    )]) {
                        sh "echo ${DOCKERHUB_PASS} | docker login -u ${DOCKERHUB_USER} --password-stdin"
                    }
                    
                    sh "docker push ${env.DOCKER_IMAGE_NAME}"
                    
                    def baseImageName = env.DOCKER_IMAGE_NAME.split(':')[0]
                    sh """
                        docker tag ${env.DOCKER_IMAGE_NAME} ${baseImageName}:latest
                        docker push ${baseImageName}:latest
                    """
                }
            }
        }

        stage('Send Email Notification') {
    steps {
        emailext(
            to: 'sarahmaamar2001@gmail.com',
            subject: "Build #${env.BUILD_NUMBER} - ${currentBuild.currentResult}",
            body: """
            <h3>Build Status: ${currentBuild.currentResult}</h3>
            <p><b>Build #:</b> ${env.BUILD_NUMBER}</p>
            <p><b>Logs:</b> <a href="${env.BUILD_URL}console">View Console Output</a></p>
            <p><b>Application:</b> <a href="http://172.25.249.157:8082/Foyer">Open Foyer</a></p>
            """,
            mimeType: 'text/html'
        )
    }
}
    }

    post {
        success {
            echo 'Build, analysis, and deployment completed successfully!'
            archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true
        }
        failure {
            echo 'There was an error in the build process.'
        }
    }
}
