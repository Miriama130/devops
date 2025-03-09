pipeline {
    agent any

    environment {
        DOCKER_IMAGE = 'miriama13/foyer-app'
        DOCKER_TAG = 'v1'
        SONARQUBE_URL = 'http://172.20.99.98:9000/'
        NEXUS_URL = 'http://172.20.99.98:8081/repository/maven-releases/'
    }

    stages {
        stage('Checkout SCM') {
            steps {
                checkout scm
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

        stage('SonarQube Analysis') {
            steps {
                echo '🔍 Analyse du code avec SonarQube...'
                script {
                    withCredentials([string(credentialsId: 'sonarqubetoken', variable: 'SONAR_TOKEN')]) {
                        sh '''
                            mvn sonar:sonar \
                                -Dsonar.host.url=$SONARQUBE_URL \
                                -Dsonar.login=$SONAR_TOKEN
                        '''
                    }
                }
            }
        }

        stage('Construction du livrable') {
            steps {
                echo '🔨 Construction du livrable sans exécuter les tests...'
                sh 'mvn package -DskipTests'
            }
        }

        stage('Push to Docker Hub') {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: 'dockercredentials', usernameVariable: 'DOCKER_USERNAME', passwordVariable: 'DOCKER_PASSWORD')]) {
                        sh '''
                            echo "$DOCKER_PASSWORD" | docker login -u "$DOCKER_USERNAME" --password-stdin
                            docker tag "$DOCKER_IMAGE:latest" "$DOCKER_IMAGE:$DOCKER_TAG"
                            docker push "$DOCKER_IMAGE:$DOCKER_TAG"
                            docker logout
                        '''
                    }
                }
            }
        }

        stage('Déploiement sur Nexus') {
            steps {
                echo '📦 Déploiement du livrable sur Nexus...'
                script {
                    echo "Vérification de la connexion à Nexus..."
                    echo "NEXUS_URL: $NEXUS_URL"
                    echo "NEXUS_USER: $NEXUS_USER"
                    echo "NEXUS_PASSWORD: $NEXUS_PASSWORD"

                    // Vérification de l'artefact avant le déploiement
                    sh 'ls -la target' // Vérifie la présence de l'artefact

                    withCredentials([usernamePassword(credentialsId: 'nexus', usernameVariable: 'NEXUS_USER', passwordVariable: 'NEXUS_PASSWORD')]) {
                        sh '''
                            mvn deploy -X \
                                -DaltDeploymentRepository=nexus::default::$NEXUS_URL \
                                -Dnexus.username=$NEXUS_USER \
                                -Dnexus.password=$NEXUS_PASSWORD \
                                -DskipTests
                        '''
                    }
                }
            }
        }

        stage('Archive artifacts') {
            steps {
                echo '📦 Archivage du livrable...'
                
                // Liste les artefacts pour vérifier leur présence
                sh 'ls -la target'  // Liste tous les fichiers dans le répertoire target

                // Archivage des artefacts
                archiveArtifacts artifacts: '**/target/*.jar', allowEmptyArchive: true
            }
        }
    }

    post {
        success {
            echo "🎉 Build, déploiement et nettoyage terminés avec succès!"
        }
        failure {
            echo "❌ Une erreur s'est produite pendant le pipeline."
        }
    }
}
