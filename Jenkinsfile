pipeline {
    agent any

    environment {
        DOCKER_IMAGE = "onsdachraoui/foyer-app:latest"
    }

    stages {
        stage('Clone Repository') {
            steps {
                script {
                    checkout([
                        $class: 'GitSCM',
                        branches: [[name: 'ons']],
                        userRemoteConfigs: [[
                            url: 'https://github.com/Miriama130/devops.git',
                            credentialsId: 'Onsgit'
                        ]]
                    ])
                }
            }
        }

        stage('Setup Maven') {
            steps {
                sh 'echo "Setting up Maven..."'
                sh 'mvn --version'
            }
        }

        stage('Maven Build') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Run Unit Tests') {
            steps {
                sh 'mvn test'
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    // Pull the base image (only if not already cached)
                    sh 'docker pull openjdk:17-jdk-alpine || true'
                    // Build the Docker image using the defined DOCKER_IMAGE variable
                    sh 'docker build -t ${DOCKER_IMAGE} .'
                }
            }
        }

        stage('Push to Docker Hub') {
            steps {
                script {
                    // Docker login (ensure credentials are set in Jenkins)
                    withCredentials([usernamePassword(credentialsId: 'onsDocker', usernameVariable: 'DOCKER_USERNAME', passwordVariable: 'DOCKER_PASSWORD')]) {
                        sh 'echo $DOCKER_PASSWORD | docker login --username $DOCKER_USERNAME --password-stdin'
                    }
                    // Push the Docker image to Docker Hub
                    sh 'docker push ${DOCKER_IMAGE}'
                }
            }
        }

        stage('Run Tests with Spring Profile') {
            steps {
                sh 'mvn test -Dspring.profiles.active=test'
            }
        }
    }
}
