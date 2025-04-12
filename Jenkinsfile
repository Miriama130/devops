pipeline {
    agent any
    
    environment {
        SONARQUBE_URL = 'http://localhost:9000'  
        SONARQUBE_TOKEN = credentials('sonarqube-token')
        NEXUS_URL = 'http://localhost:8081/repository/maven-releases/'
        NEXUS_CREDENTIALS = credentials('nexus-credentials')
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
                    // Create settings.xml file with Nexus credentials
                    sh '''
                        cat > settings.xml <<EOF
                        <settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
                          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
                                              https://maven.apache.org/xsd/settings-1.0.0.xsd">
                          <servers>
                            <server>
                              <id>nexus-releases</id>
                              <username>${NEXUS_CREDENTIALS_USR}</username>
                              <password>${NEXUS_CREDENTIALS_PSW}</password>
                            </server>
                          </servers>
                        </settings>
                        EOF
                    '''
                    
                    // Deploy to Nexus
                    withCredentials([usernamePassword(
                        credentialsId: 'nexus-credentials',
                        usernameVariable: 'NEXUS_USER',
                        passwordVariable: 'NEXUS_PASS'
                    )]) {
                        sh """
                            mvn deploy \
                            -DaltDeploymentRepository=nexus-releases::default::${NEXUS_URL} \
                            -DrepositoryId=nexus-releases \
                            -s settings.xml
                        """
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
