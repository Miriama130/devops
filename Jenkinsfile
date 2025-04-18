pipeline {
    agent any

    environment {
        // General settings
        IMAGE_NAME = 'foyer-app'
        IMAGE_TAG = "latest-${BUILD_NUMBER}"
        CONTAINER_PORT = 8081
        HOST_PORT = 8081

        // SonarQube settings
        SONAR_HOST_URL = 'http://172.17.102.63:9000'
        SONAR_PROJECT_KEY = 'devops'
        SONAR_PROJECT_NAME = 'devops'

        // Nexus settings (UPDATE THESE)
        NEXUS_URL = 'http://your-nexus-server:8081'  // e.g., http://nexus.example.com:8081
        NEXUS_DOCKER_REPO = 'your-docker-repo'       // e.g., docker-hosted
        NEXUS_MAVEN_REPO = 'your-maven-repo'         // e.g., maven-releases
        NEXUS_CREDS = credentials('nexus-credentials') // Jenkins credentials ID for Nexus
    }

    stages {
        stage('Checkout') {
            steps { checkout scm }
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
                    withCredentials([string(credentialsId: 'devopes', variable: 'SONAR_TOKEN')]) {
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

        stage('Build & Push Docker Image to Nexus') {
            when { expression { currentBuild.resultIsBetterOrEqualTo('SUCCESS') } }
            steps {
                script {
                    // Build Docker image
                    sh "docker build -t ${env.IMAGE_NAME}:${env.IMAGE_TAG} ."

                    // Tag for Nexus
                    sh "docker tag ${env.IMAGE_NAME}:${env.IMAGE_TAG} ${env.NEXUS_URL}/${env.NEXUS_DOCKER_REPO}/${env.IMAGE_NAME}:${env.IMAGE_TAG}"

                    // Login to Nexus Docker registry
                    sh "echo ${env.NEXUS_CREDS_PSW} | docker login -u ${env.NEXUS_CREDS_USR} --password-stdin ${env.NEXUS_URL}"

                    // Push to Nexus
                    sh "docker push ${env.NEXUS_URL}/${env.NEXUS_DOCKER_REPO}/${env.IMAGE_NAME}:${env.IMAGE_TAG}"

                    // Clean up
                    sh "docker rmi ${env.IMAGE_NAME}:${env.IMAGE_TAG} ${env.NEXUS_URL}/${env.NEXUS_DOCKER_REPO}/${env.IMAGE_NAME}:${env.IMAGE_TAG} || true"
                }
            }
        }

        stage('Deploy Maven Artifact to Nexus') {
            when { expression { currentBuild.resultIsBetterOrEqualTo('SUCCESS') } }
            steps {
                script {
                    // Deploy JAR to Nexus using Maven
                    sh """
                        mvn deploy:deploy-file \
                        -DgroupId=\$(mvn help:evaluate -Dexpression=project.groupId -q -DforceStdout) \
                        -DartifactId=\$(mvn help:evaluate -Dexpression=project.artifactId -q -DforceStdout) \
                        -Dversion=\$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout) \
                        -Dpackaging=jar \
                        -Dfile=target/\$(mvn help:evaluate -Dexpression=project.build.finalName -q -DforceStdout).jar \
                        -DrepositoryId=nexus \
                        -Durl=${env.NEXUS_URL}/repository/${env.NEXUS_MAVEN_REPO} \
                        -s settings.xml
                    """
                }
            }
        }

        stage('Deploy Locally (Optional)') {
            when { expression { currentBuild.resultIsBetterOrEqualTo('SUCCESS') } }
            steps {
                script {
                    sh "docker stop devops-app || true"
                    sh "docker rm devops-app || true"
                    sh """
                        docker run -d \
                        -p ${env.HOST_PORT}:${env.CONTAINER_PORT} \
                        --name devops-app \
                        ${env.NEXUS_URL}/${env.NEXUS_DOCKER_REPO}/${env.IMAGE_NAME}:${env.IMAGE_TAG}
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
        success { echo 'Pipeline succeeded!' }
        failure { echo 'Pipeline failed!' }
    }
}