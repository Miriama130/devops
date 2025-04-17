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
        RECIPIENT = 'zayneb.guasmi@gmail.com'
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
                    sh '''
                        echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
                        docker push ${DOCKER_REGISTRY}/${IMAGE_NAME}:${IMAGE_TAG}
                    '''
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
    }

    post {
        always {
            echo "Pipeline completed!"
        }
        success {
            echo "✅ Build succeeded!"
            emailext(
                subject: "✅ Build Réussi: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                body: "Bonjour,<br><br>Le build <b>${env.JOB_NAME} #${env.BUILD_NUMBER}</b> a été exécuté avec succès.<br>Voir les détails ici : <a href='${env.BUILD_URL}'>Lien Jenkins</a>",
                recipientProviders: [[$class: 'DevelopersRecipientProvider']],
                to: "${RECIPIENT}"
            )
        }
        failure {
            echo "❌ Build failed!"
            emailext(
                subject: "❌ Échec du Build: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                body: "Bonjour,<br><br>Le build <b>${env.JOB_NAME} #${env.BUILD_NUMBER}</b> a échoué.<br>Voir les logs ici : <a href='${env.BUILD_URL}'>Lien Jenkins</a>",
                recipientProviders: [[$class: 'DevelopersRecipientProvider']],
                to: "${RECIPIENT}"
            )
        }
    }
}
