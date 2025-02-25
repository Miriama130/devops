pipeline {
    agent any

    environment {
        DOCKER_REGISTRY = 'guesmizaineb'  // Remplacer par ton nom Docker Hub
               IMAGE_NAME = 'alpine'
               IMAGE_TAG = 'latest'
               GIT_CREDENTIALS_ID = 'ZAINEB' //  DAssurez-vous que 'ZAINEB' est correctement stocké dans Jenkins Credentials
    }

    triggers {
        pollSCM('H/5 * * * *') // Polling SCM toutes les 5 minutes
    }

    stages {
        stage('Checkout') {
            steps {
                script {
                    git credentialsId: "${GIT_CREDENTIALS_ID}",
                        url: 'https://github.com/Miriama130/devops.git',
                        branch: 'zaineb'
                }
            }
        }

        stage('Clean Project') {
            steps {
                sh 'echo "Cleaning project..."'
                // Ajoutez ici des étapes réelles de nettoyage si nécessaire
            }
        }

        stage('Build Maven') {
            steps {
                sh 'mvn clean package ' //-DskipTests Exécute la construction Maven en ignorant les tests
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    // Construction de l'image Docker à partir du Dockerfile dans le répertoire courant
                    sh 'docker build -t $DOCKER_IMAGE .'
                }
            }
        }

        stage('Push to Docker Hub') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'zainebDocker', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    script {
                        // Connexion à Docker Hub
                        sh 'docker login -u $DOCKER_USER -p $DOCKER_PASS $DOCKER_REGISTRY'
                        // Envoi de l'image vers Docker Hub
                        sh 'docker push $DOCKER_IMAGE'
                    }
                }
            }
        }

        stage('Deploy') {
            steps {
                script {
                    // Récupération de l'image depuis Docker Hub
                    sh 'docker pull $DOCKER_IMAGE'
                    // Déploiement de l'image en conteneur
                    sh 'docker run -d -p 8081:8081 --name devops-app $DOCKER_IMAGE'
                }
            }
        }
    }

    post {
        success {
            echo 'Pipeline exécuté avec succès!'
        }
        failure {
            echo 'L\'exécution du pipeline a échoué.'
        }
    }

    stage('Run Tests with Spring Profile') {
                steps {
                    sh 'mvn test -Dspring.profiles.active=test'
                }
            }
        }

}