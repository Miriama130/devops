pipeline {
    agent any

    environment {
        DOCKER_REGISTRY = 'guesmizaineb'
        IMAGE_NAME = 'alpine'
        IMAGE_TAG = 'latest'
        GIT_CREDENTIALS_ID = 'ZAINEB'
    }

    triggers {
        pollSCM('H/5 * * * *')
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
            }
        }

        stage('Build Maven') {
            steps {
                sh 'mvn clean package'
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    sh 'docker build -t ${DOCKER_REGISTRY}/${IMAGE_NAME}:${IMAGE_TAG} .'
                }
            }
        }

        stage('Push to Docker Hub') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'ZainebDocker', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    script {
                        sh 'echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin'
                        sh 'docker push ${DOCKER_REGISTRY}/${IMAGE_NAME}:${IMAGE_TAG}'
                    }
                }
            }

        }

        stage('Deploy') {
            steps {
                script {
                    sh 'docker pull ${DOCKER_REGISTRY}/${IMAGE_NAME}:${IMAGE_TAG}'
                    sh 'docker run -d -p 8081:8081 --name devops-app ${DOCKER_REGISTRY}/${IMAGE_NAME}:${IMAGE_TAG}'
                }
            }
        }

        stage('Run Tests with Spring Profile') {
            steps {
                sh 'mvn test -Dspring.profiles.active=test'
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
}
