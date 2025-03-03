pipeline {
    agent any

    environment {
        DOCKER_IMAGE = "onsdachraoui/foyer-app:latest"
    }

    stages {
        stage('Clone Repository') {
            steps {
                script {
                    echo 'Cloning Repository...'
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
                echo 'Setting up Maven...'
                sh 'mvn --version'
            }
        }

        stage('Maven Build') {
            steps {
                echo 'Building Maven Project...'
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Run Unit Tests') {
            steps {
                echo 'Running Unit Tests...'
                sh 'mvn test'
            }
        }

        stage('Build Docker Image') {
            steps {
                echo 'Building Docker Image...'
                script {
                    // Pull the base image (only if not already cached)
                    sh 'docker pull openjdk:17-jdk || true'
                    // Build the Docker image using the defined DOCKER_IMAGE variable
                    sh "docker build -t ${DOCKER_IMAGE} ."
                }
            }
        }

        stage('Push to Docker Hub') {
            steps {
                echo 'Pushing Docker Image to Docker Hub...'
                script {
                    // Docker login (ensure credentials are set in Jenkins)
                    withCredentials([usernamePassword(credentialsId: 'DOCKER', usernameVariable: 'DOCKER_USERNAME', passwordVariable: 'DOCKER_PASSWORD')]) {
                        sh 'echo $DOCKER_PASSWORD | docker login -u $DOCKER_USERNAME --password-stdin'
                    }
                    // Push the Docker image to Docker Hub
                    sh "docker push ${DOCKER_IMAGE}"
                }
            }
        }

        stage('Run Tests with Spring Profile') {
            steps {
                echo 'Running Tests with Spring Profile...'
                sh 'mvn test -Dspring.profiles.active=test'
            }
        }
    }
}
