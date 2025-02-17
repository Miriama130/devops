pipeline {
    agent any

    environment {
        GIT_CREDENTIALS_ID = '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' // Replace with your Jenkins credentials ID
    }

    triggers {
        pollSCM('* * * * *') // Polls SCM every minute (replace with webhook for efficiency)
    }

    stages {
        stage('Clone Repository') {
            steps {
                script {
                    git credentialsId: 'github-token', url: 'https://github.com/Miriama130/devops.git', branch: 'Mariemtl'
                }
            }
        }

        stage('Clean Project') {
            steps {
                sh 'mvn clean'
            }
        }

        stage('Build Deliverable') {
            steps {
                sh 'mvn package -DskipTests' // Creates JAR/WAR file in target/ without running tests
            }
        }
    }

    post {
        success {
            echo "✅ Build completed successfully! Deliverable is in the 'target/' directory."
        }
        failure {
            echo "❌ Build failed! Check logs."
        }
    }
}
