pipeline {
    agent any

    environment {
        GIT_CREDENTIALS_ID = 'ONS'
    }

    triggers {
        pollSCM('H/5 * * * *') 
    }

    stages {
        stage('Clone Repository') {
            steps {
                script {
                    git credentialsId: "${GIT_CREDENTIALS_ID}",
                        url: 'https://github.com/Miriama130/devops.git',
                        branch: 'ons'
                }
            }
        }

        stage('Clean Project') {
            steps {
                script {
                    // Clean the project (Assuming Maven)
                    sh 'mvn clean' // Replace this with your project's clean command if different
                }
            }
        }

        stage('Test Unitaires') {
            steps {
                script {
                    
                    sh 'mvn test' 
                }
            }
        }
    }

    post {
        success {
            echo "✅ Build completed successfully!"
        }
        failure {
            echo "❌ Build failed! Check logs."
        }
    }
}
