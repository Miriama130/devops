pipeline {
    agent any
    
    environment {
        SONARQUBE_URL = 'http://172.20.99.98:9000/'  
        SONARQUBE_TOKEN = credentials('sonarqubetoken')  
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
                    sh 'mvn test'  // Run unit tests
                }
            }
        }

        stage('SonarQube Analysis') {
        steps {
            echo '🔍 Analyse du code avec SonarQube...'
                script {
                    sh """
                        mvn sonar:sonar \
                            -Dsonar.host.url=${SONARQUBE_URL} \
                            -Dsonar.login=${SONARQUBE_TOKEN}
                    """
                }
            }
        }


    }

    post {
        success {
            echo 'Build and clean completed successfully!'
        }
        failure {
            echo 'There was an error in the build process.'
        }
    }
}
