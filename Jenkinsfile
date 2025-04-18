pipeline {
    agent any

    environment {
        REPO_URL = 'https://github.com/Miriama130/devops.git'
        BRANCH = 'mahmoud'
        IMAGE_NAME = 'latest'
        CONTAINER_NAME = 'latest'
        SONAR_PROJECT_KEY = 'devops-mahmoud'
        SONAR_HOST_URL = 'http://172.19.114.235:9000'
        GIT_CREDENTIALS = credentials('mahmoud-d') // must match your Jenkins credentials ID
        SONAR_TOKEN = credentials('sonarqube-token') // must match your Jenkins credentials ID
    }

    stages {
        stage('Clone Repository') {
            steps {
                checkout([$class: 'GitSCM',
                    branches: [[name: "${env.BRANCH}"]],
                    userRemoteConfigs: [[
                        url: "${env.REPO_URL}",
                        credentialsId: "${env.GIT_CREDENTIALS}"
                    ]]
                ])
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    sh "docker build -t ${env.IMAGE_NAME} ."
                }
            }
        }

        stage('Run Docker Container') {
            steps {
                script {
                    sh "docker run -d --name ${env.CONTAINER_NAME} ${env.IMAGE_NAME}"
                }
            }
        }

        stage('Run Unit Tests') {
            steps {
                script {
                    sh "docker exec ${env.CONTAINER_NAME} mvn test"
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                script {
                    sh """
                        docker exec ${env.CONTAINER_NAME} mvn sonar:sonar \
                        -Dsonar.projectKey=${env.SONAR_PROJECT_KEY} \
                        -Dsonar.host.url=${env.SONAR_HOST_URL} \
                        -Dsonar.login=${env.SONAR_TOKEN}
                    """
                }
            }
        }
    }

    post {
        always {
            script {
                sh "docker stop ${env.CONTAINER_NAME} || true"
                sh "docker rm ${env.CONTAINER_NAME} || true"
                sh "docker rmi ${env.IMAGE_NAME} || true"
            }
        }
    }
}