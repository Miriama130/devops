pipeline {
    agent any

    environment {
        REPO_URL = 'https://github.com/Miriama130/devops.git'
        BRANCH = 'mahmoud'
        DOCKER_IMAGE = 'latest'
        CONTAINER_NAME = 'latest'
        SONAR_PROJECT_KEY = 'your-sonar-project-key' // Replace with your SonarQube project key
        SONAR_HOST_URL = 'http://your-sonarqube-server:9000' // Replace with your SonarQube URL
        SONAR_TOKEN = credentials('sonarqube-token') // Replace with the correct credential ID
        GIT_CREDENTIALS = credentials('mahmoud-d')
    }

    stages {
        stage('Clone Repository') {
            steps {
                script {
                    checkout([$class: 'GitSCM', branches: [[name: "${BRANCH}"]], userRemoteConfigs: [[url: "${REPO_URL}", credentialsId: 'github-credentials']]])
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    sh "docker build -t ${DOCKER_IMAGE} ."
                }
            }
        }

        stage('Run Docker Container') {
            steps {
                script {
                    sh "docker run -d --name ${CONTAINER_NAME} ${DOCKER_IMAGE}"
                }
            }
        }

        stage('Run Tests') {
            steps {
                script {
                    sh "docker exec ${CONTAINER_NAME} mvn test"
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                script {
                    sh "docker exec ${CONTAINER_NAME} mvn sonar:sonar -Dsonar.projectKey=${SONAR_PROJECT_KEY} -Dsonar.host.url=${SONAR_HOST_URL} -Dsonar.login=${SONAR_TOKEN}"
                }
            }
        }
    }

    post {
        always {
            script {
                sh "docker stop ${CONTAINER_NAME} || true"
                sh "docker rm ${CONTAINER_NAME} || true"
                sh "docker rmi ${DOCKER_IMAGE} || true"
            }
        }
    }
}