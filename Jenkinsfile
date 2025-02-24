pipeline {
    agent any

   

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
