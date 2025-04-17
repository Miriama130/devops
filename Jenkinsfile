pipeline {
    agent any

    environment {
        // Docker image configuration for local Docker Desktop
        IMAGE_NAME = 'foyer-app'
        IMAGE_TAG = "latest"

        // SonarQube configuration
        SONAR_HOST_URL = 'http://172.17.102.63:9000'
        SONAR_PROJECT_KEY = 'devops'
        SONAR_PROJECT_NAME = 'devops'
    }

    triggers {
        pollSCM('H/5 * * * *')
    }

    stages {
        stage('Checkout') {
            steps {
                script {
                    checkout([
                        $class: 'GitSCM',
                        branches: [[name: '*/adam']],
                        extensions: [],
                        userRemoteConfigs: [[
                            url: 'https://github.com/Miriama130/devops.git'
                        ]]
                    ])
                }
            }
        }

        stage('Clean') {
            steps {
                sh 'mvn clean'
            }
        }

        stage('Compile') {
            steps {
                sh 'mvn compile'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    withCredentials([string(credentialsId: 'sonar-token', variable: 'SONAR_TOKEN')]) {
                        sh """
                            mvn clean verify sonar:sonar \\
                            -Dsonar.projectKey=${SONAR_PROJECT_KEY} \\
                            -Dsonar.projectName=${SONAR_PROJECT_NAME} \\
                            -Dsonar.host.url=${SONAR_HOST_URL} \\
                            -Dsonar.login=${SONAR_TOKEN}
                        """
                    }
                }
            }
        }

        stage('Build Application') {
            steps {
                sh 'mvn package -DskipTests'
            }
        }

        stage('Run Tests') {
            steps {
                sh 'mvn test'
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    sh "docker build -t ${IMAGE_NAME}:${IMAGE_TAG} ."
                }
            }
        }

        stage('Deploy Locally') {
            steps {
                script {
                    sh "docker stop devops-app || true"
                    sh "docker rm devops-app || true"
                    sh "docker run -d -p 8081:8081 --name devops-app ${IMAGE_NAME}:${IMAGE_TAG}"
                }
            }
        }
    }
}
