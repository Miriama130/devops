pipeline {
    agent any

    environment {
        // Docker configuration
        DOCKER_REGISTRY = 'guesmizaineb'
        IMAGE_NAME = 'foyer-app'
        IMAGE_TAG = "latest"

        // SonarQube configuration
        SONAR_HOST_URL = 'http://172.19.129.224:9000'
        SONAR_PROJECT_KEY = 'foyer-app'
    }

    triggers {
        pollSCM('H/5 * * * *')
    }

    stages {
        stage('Checkout') {
            steps {
                script {
                    // Git credentials will be handled by Jenkins credentials binding
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
               def mvn = tool 'Default Maven';
               withSonarQubeEnv() {
                 sh "${mvn}/bin/mvn clean verify sonar:sonar -Dsonar.projectKey=devo -Dsonar.projectName='devo'"
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
                    sh "docker build -t ${DOCKER_REGISTRY}/${IMAGE_NAME}:${IMAGE_TAG} ."
                }
            }
        }

        stage('Push to Docker Hub') {
            steps {
                script {
                    // Docker credentials will be injected by Jenkins
                    withCredentials([usernamePassword(
                        usernameVariable: 'DOCKER_USER',
                        passwordVariable: 'DOCKER_PASS'
                    )]) {
                        sh """
                            echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
                            docker push ${DOCKER_REGISTRY}/${IMAGE_NAME}:${IMAGE_TAG}
                            docker logout
                        """
                    }
                }
            }
        }

        stage('Deploy') {
            steps {
                script {
                    sh "docker stop devops-app || true"
                    sh "docker rm devops-app || true"
                    sh "docker pull ${DOCKER_REGISTRY}/${IMAGE_NAME}:${IMAGE_TAG}"
                    sh "docker run -d -p 8081:8081 --name devops-app ${DOCKER_REGISTRY}/${IMAGE_NAME}:${IMAGE_TAG}"
                }
            }
        }
    }
}