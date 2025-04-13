pipeline {
    agent any
    environment {
        DOCKER_IMAGE = 'miriama13/foyer-app'
        DOCKER_TAG = 'v1'
        SONARQUBE_URL = 'http://172.20.99.98:9000/'
    }
    stages {
        stage('Checkout SCM') {
            steps {
                checkout([
                    $class: 'GitSCM',
                    branches: [[name: 'refs/heads/zaineb']],
                    extensions: [],
                    userRemoteConfigs: [[
                        credentialsId: 'vv',
                        url: 'https://github.com/Miriama130/devops.git'
                    ]]
                ])
            }
        }
        // Add other stages (Build, Test, SonarQube, etc.)
    }
    post {
        failure {
            echo "❌ Pipeline failed! Check the logs."
        }
    }
}