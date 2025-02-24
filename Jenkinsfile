pipeline {
    agent any

    environment {
        DOCKER_IMAGE = 'miriama13/foyer-app'
        DOCKER_TAG = 'v1'
        DOCKER_USERNAME = 'miriama13'
        DOCKER_PASSWORD = 'mimi52150980'
    }

   stage('Nettoyage du projet') {
            steps {
                echo '🧹 Nettoyage des fichiers temporaires...'
                sh 'mvn clean'
            }
        }

        stage('Compilation & Tests') {
            steps {
                echo '🔬 Compilation et exécution des tests...'
                sh 'mvn test'
            }
        }

        stage('Construction du livrable') {
            steps {
                echo '🔨 Construction du livrable sans exécuter les tests...'
                sh 'mvn package -DskipTests'
            }
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
                    sh 'mvn clean install' // Clean and build the project
                }
            }
        }

        stage('Push to Docker Hub') {
            steps {
                script {
                    sh "echo ${DOCKER_PASSWORD} | docker login -u ${DOCKER_USERNAME} --password-stdin"
                    sh "docker push ${DOCKER_IMAGE}:${DOCKER_TAG}"
                    sh "docker logout"
                }
            }
        }

        stage('Archive artifacts') {
            steps {
                echo 'Archiving the JAR file'
                archiveArtifacts artifacts: '*/target/.jar', allowEmptyArchive: true
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
