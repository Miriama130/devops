pipeline {
    agent any

    environment {
        IMAGE_NAME = 'foyer-app'
        IMAGE_TAG = "latest-${BUILD_NUMBER}"
        CONTAINER_PORT = 8081
        HOST_PORT = 8081
        SONAR_HOST_URL = 'http://172.17.102.63:9000'
        SONAR_PROJECT_KEY = 'devops'
        SONAR_PROJECT_NAME = 'devops'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build & Test') {
            steps {
                // Run tests with coverage collection using verify instead of package to include integration tests
                sh 'mvn clean org.jacoco:jacoco-maven-plugin:prepare-agent verify'

                // Generate the coverage report
                sh 'mvn jacoco:report'

                // Verify coverage files exist
                sh '''
                    echo "Build artifacts:"
                    ls -la target/
                    echo "Test results:"
                    ls -la target/surefire-reports/ || echo "No test reports found"
                    echo "Coverage reports:"
                    ls -la target/site/jacoco/ || echo "No coverage reports found"
                    echo "Jacoco exec file:"
                    ls -la target/jacoco.exec || echo "No jacoco.exec found"
                '''
            }
            post {
                always {
                    junit 'target/surefire-reports/**/*.xml'
                    archiveArtifacts artifacts: 'target/site/jacoco/jacoco.xml', fingerprint: true
                    archiveArtifacts artifacts: 'target/jacoco.exec', fingerprint: true
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('Sonar') {
                    withCredentials([string(credentialsId: 'devopes', variable: 'SONAR_TOKEN')]) {
                        sh """
                            mvn sonar:sonar \\
                            -Dsonar.projectKey=${env.SONAR_PROJECT_KEY} \\
                            -Dsonar.projectName=${env.SONAR_PROJECT_NAME} \\
                            -Dsonar.host.url=${env.SONAR_HOST_URL} \\
                            -Dsonar.login=${SONAR_TOKEN} \\
                            -Dsonar.java.binaries=target/classes \\
                            -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml \\
                            -Dsonar.jacoco.reportPaths=target/jacoco.exec \\
                            -Dsonar.jacoco.reportMissing.force.zero=true \\
                            -Dsonar.sourceEncoding=UTF-8 \\
                            -Dsonar.scm.disabled=true
                        """
                    }
                }
            }
        }

        stage('Build Docker Image') {
            when {
                expression { currentBuild.resultIsBetterOrEqualTo('SUCCESS') }
            }
            steps {
                script {
                    // Add Docker version check
                    sh 'docker --version'

                    // Build with error handling
                    sh """
                        docker build -t ${env.IMAGE_NAME}:${env.IMAGE_TAG} . || \
                        { echo "Docker build failed"; exit 1; }
                    """
                }
            }
        }

        stage('Deploy Locally') {
            when {
                expression { currentBuild.resultIsBetterOrEqualTo('SUCCESS') }
            }
            steps {
                script {
                    // Stop and remove existing container with proper error handling
                    sh 'docker stop devops-app || echo "No container to stop"'
                    sh 'docker rm devops-app || echo "No container to remove"'

                    // Run with proper error handling and health check
                    sh """
                        docker run -d \\
                        -p ${env.HOST_PORT}:${env.CONTAINER_PORT} \\
                        --name devops-app \\
                        ${env.IMAGE_NAME}:${env.IMAGE_TAG} || \
                        { echo "Docker run failed"; exit 1; }
                    """

                    // Verify container is running
                    sh 'sleep 5' // Wait for container to start
                    sh 'docker ps -a | grep devops-app'
                    sh 'docker logs devops-app --tail 20'
                }
            }
        }
    }

    post {
        always {
            archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            script {
                // Clean up workspace
                echo "Cleaning up workspace..."

                // Additional cleanup if needed
                sh 'docker ps -aq | xargs --no-run-if-empty docker stop || true'
                sh 'docker ps -aq | xargs --no-run-if-empty docker rm || true'
            }
        }
        success {
            echo 'Pipeline succeeded!'
        }
        failure {
            echo 'Pipeline failed!'
            script {
                // Additional failure diagnostics
                sh 'docker ps -a'
                sh 'docker logs devops-app --tail 50 || true'
            }
        }
    }
}