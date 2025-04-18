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
        IMAGE_TAG = "${env.BUILD_ID}"  // Added missing variable
        HOST_PORT = "8080"             // Added missing variable
        CONTAINER_PORT = "8080"        // Added missing variable
    }

    stages {
        stage('Checkout') {
            steps {
                checkout([$class: 'GitSCM',
                         branches: [[name: env.BRANCH]],
                         userRemoteConfigs: [[url: env.REPO_URL, credentialsId: env.GIT_CREDENTIALS]]])
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

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('Sonar') {
                    sh """
                        mvn sonar:sonar \
                        -Dsonar.projectKey=${env.SONAR_PROJECT_KEY} \
                        -Dsonar.projectName=${env.SONAR_PROJECT_NAME} \
                        -Dsonar.host.url=${env.SONAR_HOST_URL} \
                        -Dsonar.login=${env.SONAR_TOKEN} \
                        -Dsonar.java.binaries=target/classes \
                        -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml \
                        -Dsonar.jacoco.reportPaths=target/jacoco.exec
                    """
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
                    sh "docker stop ${env.CONTAINER_NAME} || true"
                    sh "docker rm ${env.CONTAINER_NAME} || true"
                    sh """
                        docker run -d \
                        -p ${env.HOST_PORT}:${env.CONTAINER_PORT} \
                        --name ${env.CONTAINER_NAME} \
                        ${env.IMAGE_NAME}:${env.IMAGE_TAG}
                    """
                }
            }
        }
    }

    post {
        always {
            script {
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
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