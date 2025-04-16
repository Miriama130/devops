pipeline {
    agent any

    environment {
        DOCKER_IMAGE = 'miriama13/foyer-app'
        DOCKER_TAG = 'latest'
        SONARQUBE_URL = 'http://172.20.99.98:9000'
        NEXUS_URL = 'http://172.20.99.98:8081/repository/maven-releases/'
        ARTIFACT_VERSION = '0.0.1'
        ARTIFACT_NAME = 'Foyer'
        ARTIFACT_PATH = "tn/esprit/spring/${ARTIFACT_NAME}/${ARTIFACT_VERSION}/${ARTIFACT_NAME}-${ARTIFACT_VERSION}.jar"
    }

    stages {
        stage('Clean Docker Environment') {
            steps {
                sh '''
                    # Clean up application containers if they exist
                    docker-compose -f docker-compose.yml down || true
                    
                    # Remove specific containers by name if they still exist
                    docker rm -f spring-foyer mysql-container || true
                    
                    # Remove old images
                    docker rmi -f ${DOCKER_IMAGE}:${DOCKER_TAG} || true
                '''
            }
        }

        stage('Checkout Code') {
            steps {
                git branch: 'Mariemtl',
                    credentialsId: 'Token',
                    url: 'https://github.com/Miriama130/devops.git'
            }
        }

        stage('Build & Test') {
            steps {
                sh 'mvn clean package'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withCredentials([string(credentialsId: 'sonarqubetoken', variable: 'SONAR_TOKEN')]) {
                    sh '''
                        mvn sonar:sonar \
                        -Dsonar.projectKey=FoyerApp \
                        -Dsonar.host.url=${SONARQUBE_URL} \
                        -Dsonar.login=${SONAR_TOKEN}
                    '''
                }
            }
        }

        stage('Deploy to Nexus') {
    steps {
        script {
            withCredentials([usernamePassword(
                credentialsId: 'nexus',
                usernameVariable: 'NEXUS_USER',
                passwordVariable: 'NEXUS_PASS'
            )]) {
                // Make sure settings.xml exists in workspace
                sh 'ls -la settings.xml || echo "settings.xml not found"'
                
                sh """
                    mvn deploy \
                    -DaltDeploymentRepository=nexus-releases::default::${NEXUS_URL} \
                    -DrepositoryId=nexus-releases \
                    -s settings.xml
                """
            }
        }
    }
}

        stage('Build Docker Image') {
            steps {
                script {
                    sh "docker build -t ${DOCKER_IMAGE}:${DOCKER_TAG} ."
                    sh 'docker images | grep ${DOCKER_IMAGE}'
                }
            }
        }

        stage('Push to Docker Hub') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'dockercredentials', 
                    usernameVariable: 'DOCKER_USERNAME', 
                    passwordVariable: 'DOCKER_PASSWORD'  
                )]) {
                    sh "echo ${DOCKER_PASSWORD} | docker login -u ${DOCKER_USERNAME} --password-stdin"
                    sh "docker push ${DOCKER_IMAGE}:${DOCKER_TAG}"
                    sh "docker logout"
                }
            }
        }

        stage('Deploy Application') {
            steps {
                sh 'docker-compose -f docker-compose.yml up -d'
                sh 'sleep 20'
                
                // Verify application is running
                sh """
                    curl -s http://localhost:8082/Foyer/actuator/health || echo "Health check failed"
                    echo "Application should be available at: http://172.20.99.98:8082/Foyer"
                """
            }
        }
    }

    post {
        success {
            echo "Pipeline executed successfully!"
            echo "Artifacts deployed to Nexus: ${NEXUS_URL}"
            echo "Docker Image: ${DOCKER_IMAGE}:${DOCKER_TAG}"
            echo "Application deployed at: http://172.20.99.98:8082/Foyer"
        }
        failure {
            echo "Pipeline failed. Check the logs for errors."
        }
    }
}
