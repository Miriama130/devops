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

        stage("Publish to Nexus Repository Manager") {
            steps {
                script {
                    pom = readMavenPom file: "pom.xml";
                    filesByGlob = findFiles(glob: "target/*.${pom.packaging}");
                    echo "${filesByGlob[0].name} ${filesByGlob[0].path} ${filesByGlob[0].directory} ${filesByGlob[0].length} ${filesByGlob[0].lastModified}"
                    artifactPath = filesByGlob[0].path;
                    artifactExists = fileExists artifactPath;
                    if(artifactExists) {
                        echo "*** File: ${artifactPath}, group: ${pom.groupId}, packaging: ${pom.packaging}, version ${pom.version}";
                        nexusArtifactUploader(
                            nexusVersion: 'nexus3',
                            protocol: 'http',
                            nexusUrl: NEXUS_URL,
                            groupId: 'pom.tn.esprit.spring',
                            version: 'pom.0.0.1-SNAPSHOT',
                            repository: 'mavenrepo',
                            credentialsId: 'nexus-credentials',
                            artifacts: [
                                [artifactId: 'pom.Foyer',
                                classifier: '',
                                file: artifactPath,
                                type: pom.packaging],
                                [artifactId: 'pom.Foyer',
                                classifier: '',
                                file: "pom.xml",
                                type: "pom"]
                            ]
                        );
                    } else {
                        error "*** File: ${artifactPath}, could not be found";
                    }
                }
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
