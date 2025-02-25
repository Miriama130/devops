pipeline {
    agent any

    environment {
        REPO_URL = "https://github.com/Miriama130/devops.git"
        BRANCH = "ons"
        DOCKER_IMAGE = "onsdachraoui/foyer-app"
    }

    stages {
        stage('Checkout GIT') {
            steps {
                echo '📥 Clonage du dépôt depuis GitHub...'
                withCredentials([string(credentialsId: 'ONS', variable: 'GITHUB_TOKEN')]) {
                    git branch: "${BRANCH}",
                        url: "https://github.com${GITHUB_TOKEN}@github.com/Miriama130/devops.git"
                }
            }
        }

        stage('Nettoyage du projet') {
            steps {
                echo '🧹 Nettoyage des fichiers temporaires...'
                sh 'mvn clean'
            }
        }

        stage('Compilation & Tests') {
            steps {
                echo '🔬 Compilation et exécution des tests...'
                sh 'mvn test'
            }
        }

        stage('Construction du livrable') {
            steps {
                echo '🔨 Construction du livrable sans exécuter les tests...'
                sh 'mvn package -DskipTests'
            }
        }

        stage('Construction de l\'image Docker') {
            steps {
                echo '🐳 Création de l\'image Docker...'
                sh 'docker build -t ${DOCKER_IMAGE}:latest .'
            }
        }

        stage('Push vers Docker Hub') {
            steps {
                echo '📤 Vérification et envoi de l\'image Docker...'
                withCredentials([usernamePassword(credentialsId: 'onsDocker', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    sh '''
                        echo ${DOCKER_PASS} | docker login -u ${DOCKER_USER} --password-stdin
                        docker push ${DOCKER_IMAGE}:latest
                    '''
                }
            }
        }
    }

    post {
        success {
            echo '✅ Pipeline terminé avec succès ! 🚀'
        }
        failure {
            echo '❌ Échec du pipeline, vérifiez les logs Jenkins pour plus d\'informations.'
        }
    }
}