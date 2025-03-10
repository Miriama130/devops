pipeline {
    agent any
    
    environment {
        SONARQUBE_URL = 'http://localhost:9000/'  
        SONARQUBE_TOKEN = credentials('jenkins-sonar')  
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
                        mvn sonar:sonar 
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
