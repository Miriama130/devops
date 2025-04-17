pipeline {
    agent any

    environment {
        DOCKER_IMAGE = "onsdachraoui/foyer-app:latest"
        NEXUS_REPO = "maven-snapshots"  // Remplace par "maven-snapshots" si nécessaire
        NEXUS_RELEASES_URL = "http://172.18.64.72:8081/repository/maven-releases"
        ARTIFACT_NAME = 'Foyer'    
        ARTIFACT_VERSION = '0.0.1-SNAPSHOT'
        ARTIFACT_PATH = "tn/esprit/spring/${ARTIFACT_NAME}/${ARTIFACT_VERSION}/${ARTIFACT_NAME}-${ARTIFACT_VERSION}.jar"
    }

    stages {
        stage('Clone Repository') {
            steps {
                script {
                    echo 'Cloning Repository...'
                    checkout([ 
                        $class: 'GitSCM',
                        branches: [[name: 'ons']],
                        userRemoteConfigs: [[
                            url: 'https://github.com/Miriama130/devops.git',
                            credentialsId: 'nsgit'
                        ]]
                    ])
                }
            }
        }

        stage('Setup Maven') {
            steps {
                echo 'Checking Maven version...'
                sh 'mvn --version'
            }
        }

        stage('Maven Build') {
            steps {
                echo 'Building Maven Project...'
                sh 'mvn clean package'
                sh 'ls -l target/'
            }
        }

        stage('Run Unit Tests') {
            steps {
                echo 'Running Unit Tests...'
                sh 'mvn test'
            }
        }

        stage('Verify Dockerfile') {
            steps {
                echo 'Checking Dockerfile presence...'
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
                script {
                    withCredentials([usernamePassword(credentialsId: 'DOCKER', passwordVariable: 'DOCKER_PASSWORD', usernameVariable: 'DOCKER_USERNAME')]) {
                        sh '''
                        echo $DOCKER_PASSWORD | docker login -u $DOCKER_USERNAME --password-stdin
                        docker build -t $DOCKER_IMAGE .
                        '''
                    }
                }
            }
        }

        stage('Push Docker Image') {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: 'DOCKER', passwordVariable: 'DOCKER_PASSWORD', usernameVariable: 'DOCKER_USERNAME')]) {
                        sh '''
                        echo $DOCKER_PASSWORD | docker login -u $DOCKER_USERNAME --password-stdin
                        docker push $DOCKER_IMAGE
                        '''
                    }
                }
            }
        }

        stage('Run Tests with Spring Profile') {
            steps {
                echo 'Running Tests with Spring Profile...'
                sh 'mvn test -Dspring.profiles.active=test'
            }
        }

        stage('Download Artifact from Nexus') {
            steps {
                script {
                    withCredentials([usernamePassword(
                        credentialsId: 'nex-cred',
                        usernameVariable: 'NEXUS_USER',
                        passwordVariable: 'NEXUS_PASS'
                    )]) {
                        // Create target directory if it doesn't exist
                        sh 'mkdir -p target'
                        
                        // Download the JAR from Nexus releases repository
                        sh """
                            curl -u ${NEXUS_USER}:${NEXUS_PASS} \
                            -o target/${ARTIFACT_NAME}-${ARTIFACT_VERSION}.jar \
                            "${NEXUS_RELEASES_URL}${ARTIFACT_PATH}"
                        """
                        
                        // Verify the download
                        sh 'ls -l target/'
                        sh 'file target/${ARTIFACT_NAME}-${ARTIFACT_VERSION}.jar'
                    }
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                script {
                    withSonarQubeEnv('SonarQube') {
                        withCredentials([string(credentialsId: 'JENKINS-SONAR', variable: 'SONAR_TOKEN')]) {
                            sh '''
                            mvn sonar:sonar \
                                -Dsonar.projectKey=foyer-app \
                                -Dsonar.host.url=http://172.18.64.72:9000 \
                                -Dsonar.login=$SONAR_TOKEN
                            '''
                        }
                    }
                }
            }
        }

        stage('Send Email Notification') {
            steps {
                mail(
                    to: 'onsdachraoui87@gmail.com',
                    subject: "✅ Jenkins Build #${env.BUILD_NUMBER} - ${currentBuild.currentResult}",
                    body: """
                        Hello,

                        ✅ Jenkins Build #${env.BUILD_NUMBER} has completed with status: ${currentBuild.currentResult}.

                        🔗 Build Details: ${env.BUILD_URL}
                        🐳 Docker Image: ${DOCKER_IMAGE}
                        🎯 Artifact: ${ARTIFACT_NAME}-${ARTIFACT_VERSION}.jar
                        📦 Nexus URL: ${NEXUS_RELEASES_URL}${ARTIFACT_PATH}

                        Have a nice day!
                    """
                )
            }
        }
    }

    post {
        always {
            echo "Pipeline completed!"
        }
        failure {
            echo "Pipeline failed!"
        }
        success {
            echo "Pipeline succeeded!"
        }
    }
}
