pipeline {
    agent any

    environment {
        DOCKER_IMAGE = "onsdachraoui/foyer-app:latest"
    }

    stages {
        stage('Clone Repository') {
            steps {
                script {
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
                sh 'echo "Setting up Maven..."'
                sh 'mvn --version'
            }
        }

        stage('Maven Build') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Run Unit Tests') {
            steps {
                sh 'mvn test'
            }
        }
stages {
  stage('Build') {
    steps {
      script {
        // Pull the image only if it's not already cached
        sh 'docker pull openjdk:17-jdk-alpine || true'
      }
    }
  }
}


        stage('Push to Docker Hub') {
            steps {
                script {
                    withDockerRegistry([credentialsId: 'onsDocker', url: '']) {
                        sh "docker push ${DOCKER_IMAGE}"
                    }
                }
            }
        }

        stage('Run Tests with Spring Profile') {
            steps {
                sh 'mvn test -Dspring.profiles.active=test'
            }
        }
    }
}
