pipeline {
    agent any

    environment {
        IMAGE_NAME = 'foyer-app'
        IMAGE_TAG = "latest-${BUILD_NUMBER}"
        DOCKER_REGISTRY = '' // Add if pushing to a registry
        CONTAINER_PORT = 8081
        HOST_PORT = 8081
    }

    triggers {
        pollSCM('H/5 * * * *')
    }

    stages {
        stage('Checkout') {
            steps {
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

        stage('Build & Test') {
            steps {
                sh 'mvn clean package'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    withCredentials([string(credentialsId: 'sonar-token', variable: 'SONAR_TOKEN')]) {
                        sh """
                            mvn sonar:sonar \\
                            -Dsonar.projectKey=${SONAR_PROJECT_KEY} \\
                            -Dsonar.projectName=${SONAR_PROJECT_NAME} \\
                            -Dsonar.host.url=${SONAR_HOST_URL} \\
                            -Dsonar.login=${SONAR_TOKEN}
                        """
                    }
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    sh "docker build -t ${IMAGE_NAME}:${IMAGE_TAG} ."
                    // If using a registry:
                    // sh "docker tag ${IMAGE_NAME}:${IMAGE_TAG} ${DOCKER_REGISTRY}/${IMAGE_NAME}:${IMAGE_TAG}"
                    // withCredentials([...]) { sh "docker push ${DOCKER_REGISTRY}/${IMAGE_NAME}:${IMAGE_TAG}" }
                }
            }
        }

        stage('Deploy Locally') {
            steps {
                script {
                    sh "docker stop devops-app || true"
                    sh "docker rm devops-app || true"
                    sh """
                        docker run -d \
                        -p ${HOST_PORT}:${CONTAINER_PORT} \
                        --name devops-app \
                        ${IMAGE_NAME}:${IMAGE_TAG}
                    """
                }
            }
        }
    }

    post {
        always {
            echo 'Pipeline completed - cleanup resources if needed'
            // archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            // junit 'target/surefire-reports/**/*.xml'
        }
        success {
            echo 'Pipeline succeeded!'
        }
        failure {
            echo 'Pipeline failed!'
            // mail to: 'team@example.com', subject: 'Pipeline Failed', body: '...'
        }
    }
}