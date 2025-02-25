pipeline {
    agent any

    environment {
        REPO_URL = "https://github.com/Miriama130/devops.git"
        BRANCH = "ons"
        DOCKER_IMAGE = "onsdachraoui/foyer-app"
        IMAGE_TAG = "latest"
    }

    options {
        timeout(time: 30, unit: 'MINUTES') // Set a timeout for the pipeline
        timestamps() // Add timestamps to logs
    }

    stages {
        stage('Checkout GIT') {
            steps {
                script {
                    echo '📥 Cloning repository from GitHub...'
                    withCredentials([string(credentialsId: 'ONS', variable: 'GITHUB_TOKEN')]) {
                        retry(3) { // Retry up to 3 times in case of failure
                            git branch: "${BRANCH}",
                                url: "https://${GITHUB_TOKEN}@github.com/Miriama130/devops.git"
                        }
                    }
                }
            }
        }

        stage('Nettoyage du projet') {
            steps {
                echo '🧹 Cleaning temporary files...'
                sh 'mvn clean'
            }
        }

        stage('Compilation & Tests') {
            steps {
                echo '🔬 Running tests and compiling project...'
                sh 'mvn test'
            }
        }

        stage('Construction du livrable') {
            steps {
                echo '🔨 Packaging the application...'
                sh 'mvn package -DskipTests'
            }
        }

        stage('Construction de l\'image Docker') {
            steps {
                script {
                    echo '🐳 Building Docker image...'
                    def commitHash = sh(script: "git rev-parse --short HEAD", returnStdout: true).trim()
                    IMAGE_TAG = commitHash

                    sh """
                        docker build -t ${DOCKER_IMAGE}:${IMAGE_TAG} .
                        docker tag ${DOCKER_IMAGE}:${IMAGE_TAG} ${DOCKER_IMAGE}:latest
                    """
                }
            }
        }

        stage('Push vers Docker Hub') {
            steps {
                script {
                    echo '📤 Pushing Docker image to Docker Hub...'
                    withCredentials([usernamePassword(credentialsId: 'onsDocker', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                        sh '''
                            echo ${DOCKER_PASS} | docker login -u ${DOCKER_USER} --password-stdin
                            docker push ${DOCKER_IMAGE}:${IMAGE_TAG}
                            docker push ${DOCKER_IMAGE}:latest
                        '''
                    }
                }
            }
        }
    }

    post {
        success {
            echo '✅ Pipeline completed successfully! 🚀'
        }
        failure {
            echo '❌ Pipeline failed, check Jenkins logs for details.'
        }
        always {
            echo '📌 Cleaning up workspace...'
            sh 'docker system prune -f || true' // Cleanup Docker cache
        }
    }
}
