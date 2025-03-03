pipeline {
    agent any

    environment {
        DOCKER_IMAGE = "onsdachraoui/foyer-app:latest"
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
                echo 'Setting up Maven...'
                sh 'mvn --version'
            }
        }

        stage('Maven Build') {
            steps {
                echo 'Building Maven Project...'
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Run Unit Tests') {
            steps {
                echo 'Running Unit Tests...'
                sh 'mvn test'
            }
        }


 stage('Build Docker Image') {
            steps {
                script {
                    // Connexion à Docker Hub avec les credentials sécurisés
                    withCredentials([usernamePassword(credentialsId: 'DOCKER', passwordVariable: 'DOCKER_PASSWORD', usernameVariable: 'DOCKER_USERNAME')]) {
                        sh '''
                        echo $DOCKER_PASSWORD | docker login -u $DOCKER_USERNAME --password-stdin
                        '''

                        // Construction de l'image Docker
                        sh 'docker build -t $DOCKER_IMAGE .'
                    }
                }
            }
        }

          stage('Push Docker Image') {
                    steps {
                        script {
                            // Push de l'image vers Docker Hub
                            withCredentials([usernamePassword(credentialsId: 'DOCKER', passwordVariable: 'DOCKER_PASSWORD', usernameVariable: 'DOCKER_USERNAME')]) {
                                sh '''
                                echo $DOCKER_PASSWORD | docker login -u $DOCKER_USERNAME --password-stdin
                                docker push $DOCKER_IMAGE
                                '''
                            }
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
    }
}
