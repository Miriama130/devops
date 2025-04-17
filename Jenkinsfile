pipeline {
    agent any

    tools {
        maven 'Maven 3.8.6'  // adjust to your configured Maven tool in Jenkins
        jdk 'Java 17'        // match your project's Java version
    }

    environment {
        SONARQUBE = 'SonarQubeServer' // name must match your SonarQube server in Jenkins config
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'adam', url: 'https://github.com/Miriama130/devops.git'
            }
        }

        stage('Clean & Compile') {
            steps {
                sh 'mvn clean compile'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv("${SONARQUBE}") {
                    sh 'mvn sonar:sonar -Dsonar.projectKey=your-project-key -Dsonar.host.url=http://your-sonar-url -Dsonar.login=your-token'
                }
            }
        }

        stage('Build Application') {
            steps {
                sh 'mvn package'
            }
        }
    }

    post {
        always {
            echo 'Pipeline finished.'
        }
        success {
            echo 'Build completed successfully.'
        }
        failure {
            echo 'Build failed.'
        }
    }
}
