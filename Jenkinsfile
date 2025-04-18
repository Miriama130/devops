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
                // Run tests with coverage collection
                sh 'mvn clean verify'

                // Verify coverage files exist
                sh 'ls -la target/'
                sh 'ls -la target/site/jacoco/ || echo "Jacoco report not found"'
            }

            post {
                always {
                    junit 'target/surefire-reports/**/*.xml'
                    archiveArtifacts artifacts: 'target/site/jacoco/jacoco.xml', fingerprint: true
                }
            }
        }

        stage('Verify Coverage') {
            steps {
                // Generate the report explicitly
                sh 'mvn jacoco:report'

                // Print coverage summary
                sh 'cat target/site/jacoco/jacoco.xml | grep -A 5 "<counter type=\\"LINE\\"" || echo "No coverage data found"'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('devo') {
                    withCredentials([string(credentialsId: 'devo', variable: 'SONAR_TOKEN')]) {
                        sh """
                            mvn sonar:sonar \\
                            -Dsonar.projectKey=${env.SONAR_PROJECT_KEY} \\
                            -Dsonar.projectName=${env.SONAR_PROJECT_NAME} \\
                            -Dsonar.host.url=${env.SONAR_HOST_URL} \\
                            -Dsonar.login=${SONAR_TOKEN} \\
                            -Dsonar.java.binaries=target/classes \\
                            -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml \\
                            -Dsonar.jacoco.reportPaths=target/jacoco.exec
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
                    sh "docker build -t ${env.IMAGE_NAME}:${env.IMAGE_TAG} ."
                }
            }
        }

        stage('Deploy Locally') {
            when {
                expression { currentBuild.resultIsBetterOrEqualTo('SUCCESS') }
            }
            steps {
                script {
                    sh "docker stop devops-app || true"
                    sh "docker rm devops-app || true"
                    sh """
                        docker run -d \
                        -p ${env.HOST_PORT}:${env.CONTAINER_PORT} \
                        --name devops-app \
                        ${env.IMAGE_NAME}:${env.IMAGE_TAG}
                    """
                }
            }
        }
    }

    post {
        always {
            archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            echo 'Pipeline completed - cleanup resources if needed'
        }
        success {
            echo 'Pipeline succeeded!'
        }
        failure {
            echo 'Pipeline failed!'
        }
    }
}