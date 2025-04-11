pipeline {
    agent any

    environment {
        DOCKER_IMAGE = "onsdachraoui/foyer-app:latest"
        NEXUS_URL = "http://172.18.64.72:8081"
        NEXUS_REPO = "maven-releases"  // Remplace par "maven-snapshots" si nécessaire
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
                            credentialsId: 'Onsgit'
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
                sh 'ls -l target/'  // Vérifie que le JAR est bien généré
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

        // stage("Deploy Artifact to Nexus") {
        //     steps {
        //         script {
        //             withCredentials([usernamePassword(credentialsId: 'NEXUS_CREDENTIALS', passwordVariable: 'NEXUS_PASSWORD', usernameVariable: 'NEXUS_USERNAME')]) {
        //                 sh '''
        //                     set -e
        //                     VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)
        //                     ARTIFACT_NAME=Foyer-${VERSION}.jar

        //                     if [ ! -f "target/${ARTIFACT_NAME}" ]; then
        //                         echo "ERROR: Artifact ${ARTIFACT_NAME} not found!"
        //                         exit 1
        //                     fi

        //                     mvn deploy:deploy-file \
        //                         -Durl=${NEXUS_URL}/repository/${NEXUS_REPO}/ \
        //                         -DrepositoryId=nexus \
        //                         -Dfile=target/${ARTIFACT_NAME} \
        //                         -DgroupId=tn.esprit.spring \
        //                         -DartifactId=Foyer \
        //                         -Dversion=${VERSION} \
        //                         -Dpackaging=jar \
        //                         -DgeneratePom=true
        //                 '''
        //             }
        //         }
        //     }
        // }

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
