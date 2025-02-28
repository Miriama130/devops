pipeline {
    agent any

    environment {
        DOCKER_REGISTRY = 'guesmizaineb'
        IMAGE_NAME = 'foyer-app'
        IMAGE_TAG = "latest"
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

        stage('Clean ') {
            steps {
                sh 'mvn clean '
            }
        }
         stage(' Compile') {
                    steps {
                        sh 'mvn  compile'
                    }
                }

        stage('SonarQube Analysis') {
            environment {
                SONARQUBE_SCANNER_HOME = tool 'SonarQube Scanner'
            }
            steps {
                withSonarQubeEnv('SonarQube') {
                    withCredentials([string(credentialsId: 'Jenkins_SonarqubeToken', variable: 'SONAR_TOKEN')]) {
                        sh '''
                        ${SONARQUBE_SCANNER_HOME}/bin/sonar-scanner \
                          -Dsonar.projectKey=foyer-app \
                          -Dsonar.sources=. \
                          -Dsonar.host.url=http://172.19.129.224:9000 \
                          -Dsonar.login=$SONAR_TOKEN
                        '''
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
                    // Stop and remove the previous container if it exists
                    sh 'docker stop devops-app || true'
                    sh 'docker rm devops-app || true'

                    // Pull the latest version and deploy
                    sh 'docker pull ${DOCKER_REGISTRY}/${IMAGE_NAME}:${IMAGE_TAG}'
                    sh 'docker run -d -p 8081:8081 --name devops-app ${DOCKER_REGISTRY}/${IMAGE_NAME}:${IMAGE_TAG}'
                }
            }
        }
    }
}
