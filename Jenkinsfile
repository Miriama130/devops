pipeline {
    agent any
    
    environment {
        SONARQUBE_URL = 'http://localhost:9000'  
        SONARQUBE_TOKEN = credentials('sonarqube-token')
        NEXUS_URL = 'http://localhost:8081/repository/maven-releases/'
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
                sh 'mvn verify' // Triggers JaCoCo report generation
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
            // Debug: Show current directory contents
            sh 'ls -la'
            
            // Create settings.xml file with Nexus credentials
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
