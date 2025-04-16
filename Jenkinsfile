pipeline {
    agent any

    environment {
        DOCKER_IMAGE = 'miriama13/foyer-app'
        DOCKER_TAG = 'latest'
        SONARQUBE_URL = 'http://172.20.99.98:9000'
        NEXUS_URL = 'http://172.20.99.98:8081/repository/maven-releases/'
        ARTIFACT_VERSION = "0.0.1-${BUILD_NUMBER}"
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

        stage('Download Artifact from Nexus') {
            steps {
                script {
                    withCredentials([usernamePassword(
                        credentialsId: 'nexus',
                        usernameVariable: 'NEXUS_USER',
                        passwordVariable: 'NEXUS_PASS'
                    )]) {
                        sh 'mkdir -p target'
                        sh """
                            curl -u ${NEXUS_USER}:${NEXUS_PASS} \
                            -o target/${ARTIFACT_NAME}-${ARTIFACT_VERSION}.jar \
                            "${NEXUS_URL}${ARTIFACT_PATH}"
                        """
                        sh 'ls -l target/'
                    }
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    sh 'ls -l target/*.jar'
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

        stage('Prepare Ports') {
            steps {
                script {
                    // Just ensure no containers are using the ports
                    sh 'docker-compose -f docker-compose.yml down || true'
                    
                    // Clean up any existing volumes if needed
                    sh 'docker volume rm dockerimage_mysql_data || true'
                }
            }
        }

        stage('Deploy Application') {
            steps {
                script {
                    try {
                        // Start containers with force recreation
                        sh 'docker-compose -f docker-compose.yml up -d --force-recreate'
                        
                        // Wait for MySQL healthcheck
                        sh '''
                            timeout 180 bash -c 'while [[ "$(docker inspect -f \'{{.State.Health.Status}}\' mysql-container)" != "healthy" ]]; do 
                                sleep 10; 
                                echo "Waiting for MySQL to become healthy..."; 
                                docker logs mysql-container || true
                            done'
                        '''
                        
                        // Check application
                        sh 'curl -s --retry 5 --retry-delay 10 http://localhost:8082/Foyer/actuator/health'
                    } catch (err) {
                        echo "Deployment failed, showing logs:"
                        sh 'docker-compose logs'
                        error("Deployment failed: ${err}")
                    }
                }
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
