pipeline {
    agent any

    environment {
        DOCKER_REGISTRY = 'guesmizaineb'
        IMAGE_NAME = 'foyer-app'
        IMAGE_TAG = "latest"
        GIT_CREDENTIALS_ID = 'ZAINEB'
        NEXUS_URL = '172.19.129.224:8081'
        NEXUS_RELEASES_URL = "http://172.19.129.224:8081/repository/maven-releases"
        NEXUS_REPO = "maven-snapshots"
        ARTIFACT_ID = 'Foyer'
        ARTIFACT_VERSION = '0.0.1-SNAPSHOT'
        ARTIFACT_PATH = "tn/esprit/spring/${ARTIFACT_ID}/${ARTIFACT_VERSION}/${ARTIFACT_ID}-${ARTIFACT_VERSION}.jar"
        RECIPIENT = 'guzaineb@gmail.com'
    }

    triggers {
        pollSCM('H 0-23/2 * * *')
    }

    stages {
        stage('Checkout') {
            steps {
                git credentialsId: "${GIT_CREDENTIALS_ID}",
                    url: 'https://github.com/Miriama130/devops.git',
                    branch: 'zaineb'
            }
        }

        stage('Verify Maven') {
            steps {
                sh 'mvn --version'
            }
        }

        stage('Clean') {
            steps {
                sh 'mvn clean'
            }
        }

        stage('Compile') {
            steps {
                sh 'mvn compile'
            }
        }

        stage('Run Unit Tests') {
            steps {
                sh 'mvn test'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    withCredentials([string(credentialsId: 'Jenkins_Sonarqube_Token', variable: 'SONAR_TOKEN')]) {
                        sh '''
                            mvn sonar:sonar \
                                -Dsonar.projectKey=foyer-app \
                                -Dsonar.host.url=http://172.19.129.224:9000 \
                                -Dsonar.login=$SONAR_TOKEN
                        '''
                    }
                }
            }
        }

        stage('Package Application') {
            steps {
                sh 'mvn package -DskipTests'
            }
        }

        stage('Verify Dockerfile Presence') {
            steps {
                sh '''
                    if [ ! -f Dockerfile ]; then
                        echo "ERROR: Dockerfile is missing!"
                        exit 1
                    fi
                    ls -l Dockerfile
                '''
            }
        }

        stage('Build Docker Image') {
            steps {
                sh """
                    docker build -t ${DOCKER_REGISTRY}/${IMAGE_NAME}:${IMAGE_TAG} .
                """
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
                sh '''
                    docker stop devops-app || true
                    docker rm devops-app || true
                    docker pull ${DOCKER_REGISTRY}/${IMAGE_NAME}:${IMAGE_TAG}
                    docker run -d -p 8083:8081 --name devops-app ${DOCKER_REGISTRY}/${IMAGE_NAME}:${IMAGE_TAG}
                '''
            }
        }
    }

        stage('Upload Artifact to Nexus') {
            steps {
                nexusArtifactUploader(
                    nexusVersion: 'nexus3',
                    protocol: 'http',
                    nexusUrl: "${NEXUS_URL}",
                    groupId: 'com.foyer',
                    version: "${ARTIFACT_VERSION}",
                    repository: "${NEXUS_REPO}",
                    credentialsId: 'NexusJenkins',
                    artifacts: [[
                        artifactId: "${ARTIFACT_ID}",
                        classifier: '',
                        file: "target/${ARTIFACT_ID}-${ARTIFACT_VERSION}.jar",
                        type: 'jar'
                    ]]
                )
            }
        }

        stage('Send Email Notification') {
            steps {
                mail(
                    to: "${RECIPIENT}",
                    subject: "✅ Jenkins Build #${env.BUILD_NUMBER} - ${currentBuild.currentResult}",
                    body: """
                        Hello,

                        ✅ Jenkins Build #${env.BUILD_NUMBER} has completed with status: ${currentBuild.currentResult}.

                        🔗 Build Details: ${env.BUILD_URL}
                        🐳 Docker Image: ${DOCKER_REGISTRY}/${IMAGE_NAME}:${IMAGE_TAG}
                        🎯 Artifact: ${ARTIFACT_ID}-${ARTIFACT_VERSION}.jar
                        📦 Nexus URL: ${NEXUS_RELEASES_URL}/${ARTIFACT_PATH}

                        Have a nice day!
                    """
                )
            }
        }
    }
}
