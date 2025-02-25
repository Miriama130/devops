pipeline {
    agent any

    environment {
        DOCKER_IMAGE = 'miriama13/foyer-app'
        DOCKER_TAG = 'v1'
    }

    stages {
        stage('Checkout SCM') {
            steps {
                checkout scm
            }
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

        stage('Push to Docker Hub') {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: 'docker-hub-credentials', usernameVariable: 'DOCKER_USERNAME', passwordVariable: 'DOCKER_PASSWORD')]) {
                        sh "echo ${DOCKER_PASSWORD} | docker login -u ${DOCKER_USERNAME} --password-stdin"
                        sh "docker tag ${DOCKER_IMAGE}:latest ${DOCKER_IMAGE}:${DOCKER_TAG}"
                        sh "docker push ${DOCKER_IMAGE}:${DOCKER_TAG}"
                        sh "docker logout"
                    }
                }
            }
        }

        stage('Archive artifacts') {
            steps {
                echo '📦 Archivage du livrable'
                archiveArtifacts artifacts: '**/target/*.jar', allowEmptyArchive: true
            }
        }
    }

    post {
        success {
            echo '🎉 Build et nettoyage terminés avec succès!'
        }
        failure {
            echo '❌ Une erreur s\'est produite pendant 
