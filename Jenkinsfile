pipeline {
    agent any
    environment {
        DOCKER_REGISTRY = 'guesmizaineb'
        IMAGE_NAME = 'foyer-app'
        IMAGE_TAG = "latest"
        GIT_CREDENTIALS_ID = 'ZAINEB'
    }

    triggers {
        pollSCM('H 0-23/2 * * *')
    }

    stages {
        stage('Checkout') {
            steps {
                script {
                    git credentialsId: "${GIT_CREDENTIALS_ID}",
                        url: 'https://github.com/Miriama130/devops.git',
                        branch: 'zaineb'
                }
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

        stage('Build Application') {
            steps {
                sh 'mvn package -DskipTests'
            }
        }

        stage('Run Tests') {
            steps {
                sh 'mvn test'
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    sh 'docker build -t ${DOCKER_REGISTRY}/${IMAGE_NAME}:${IMAGE_TAG} .'
                }
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
                script {
                    sh 'docker stop devops-app || true'
                    sh 'docker rm devops-app || true'
                    sh 'docker pull ${DOCKER_REGISTRY}/${IMAGE_NAME}:${IMAGE_TAG}'
                    sh 'docker run -d -p 8083:8081 --name devops-app ${DOCKER_REGISTRY}/${IMAGE_NAME}:${IMAGE_TAG}'
                }
            }
        }

      stage('Artifact Upload') {
    steps {
        nexusArtifactUploader(
            nexusVersion: 'nexus3',
            protocol: 'http',
            nexusUrl: '172.19.129.224:8081',
            groupId: 'com.foyer',
            version: '0.0.1-SNAPSHOT',
            repository: 'maven-central-repository',
            credentialsId: 'NexusJenkins',
            artifacts: [[
                artifactId: 'Foyer',
                classifier: '',
                file: 'target/Foyer-0.0.1-SNAPSHOT.jar',
                type: 'jar'
            ]]
        )
    }
}

    }
}
