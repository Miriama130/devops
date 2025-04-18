pipeline {
    agent any

    environment {
        REPO_URL = 'https://github.com/Miriama130/devops.git'
        BRANCH = 'mahmoud'
        IMAGE_NAME = 'latest'
        CONTAINER_NAME = 'latest'
        SONAR_PROJECT_KEY = 'devops-mahmoud'
        SONAR_HOST_URL = 'http://172.19.114.235:9000' //Sonarqube server URL
        GIT_CREDENTIALS = credentials('mahmoud-d') // make sure this ID exists
        SONAR_TOKEN = credentials('sonarqube-token') // make sure this ID exists
    }

    stages {
        stage('Clone Repository') {
            steps {
                checkout([$class: 'GitSCM',
                          branches: [[name: "${BRANCH}"]],
                          userRemoteConfigs: [[
                              url: "${REPO_URL}",
                              credentialsId: "${GIT_CREDENTIALS}"
                          ]]
                ])
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    sh "docker build -t ${IMAGE_NAME} ."
                }
            }
        }

        stage('Run Docker Container') {
            steps {
                script {
                    sh "docker run -d --name ${CONTAINER_NAME} ${IMAGE_NAME}"
                }
            }
        }

        stage('Run Unit Tests') {
            steps {
                script {
                    sh "docker exec ${CONTAINER_NAME} mvn test"
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                script {
                    // Inject token safely into container (you can also mount a volume or use docker env flags)
                    sh """
                        docker exec ${CONTAINER_NAME} \
                        mvn sonar:sonar \
                        -Dsonar.projectKey=${SONAR_PROJECT_KEY} \
                        -Dsonar.host.url=${SONAR_HOST_URL} \
                        -Dsonar.login=${SONAR_TOKEN}
                    """
                }
            }
        }
    }

    post {
        always {
            script {
                sh "docker stop ${CONTAINER_NAME} || true"
                sh "docker rm ${CONTAINER_NAME} || true"
                sh "docker rmi ${IMAGE_NAME} || true"
            }
        }
    }
}