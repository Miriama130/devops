
pipeline {
    agent any
       environment {
            DOCKER_IMAGE = 'guesmizaineb/alpine:latest'
             GIT_CREDENTIALS_ID = 'ZAINEB' // Ensure 'ZAINEB' is correctly stored in Jenkins Credentials
        }


    triggers {
        pollSCM('H/5 * * * *') // Polls SCM every 5 minutes (Reduce load)
    }

    stages {
        stage('Clone Repository') {
            steps {
                script {
                    git credentialsId: "${GIT_CREDENTIALS_ID}",
                        url: 'https://github.com/Miriama130/devops.git',
                        branch: 'zaineb'
                }
            }
        }

        stage('Clean Project') {
            steps {
                sh 'echo "helloo!"'
            }
        }

         stage('Build Project') {
                    steps {
                        sh 'mvn package -DskipTests'
                    }
                }


         stage('Build Docker Image') {
                        steps {
                            script {
                                sh "docker build -t $DOCKER_IMAGE ."
                            }
                        }
                    }






            stage('Push Docker Image') {
                       steps {
                           script {
                               withCredentials([usernamePassword(credentialsId: 'zainebDocker', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                                   sh "docker login -u $DOCKER_USER -p $DOCKER_PASS"
                                   sh "docker push $DOCKER_IMAGE"
                               }
                           }
                       }
                   }


                        stage('Pull Docker Image') {
                               steps {
                                   script {
                                       sh "docker pull $DOCKER_IMAGE"
                                   }
                               }
                        }




    }
    }
