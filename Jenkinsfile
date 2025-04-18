pipeline {
    agent any

    environment {
        REPO_URL = 'https://github.com/Miriama130/devops.git'
        BRANCH = 'mahmoud'
        IMAGE_NAME = 'latest'
        CONTAINER_NAME = 'latest'
        SONAR_PROJECT_KEY = 'devops-mahmoud'
        SONAR_HOST_URL = 'http://172.19.114.235:9000'
        GIT_CREDENTIALS = credentials('mahmoud-d')
        SONAR_TOKEN = credentials('sonar-mahmoud')
        SONAR_PROJECT_NAME = 'devops-mahmoud'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build & Test') {
            steps {
                sh 'mvn clean verify'
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
                sh 'mvn jacoco:report'
                sh 'cat target/site/jacoco/jacoco.xml | grep -A 5 "<counter type=\\"LINE\\"" || echo "No coverage data found"'
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

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('Sonar') {
                    withCredentials([string(credentialsId: 'sonarqube-token', variable: 'SONAR_TOKEN')]) {
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