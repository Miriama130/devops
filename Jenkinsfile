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
            // Utiliser withCredentials pour récupérer les informations Nexus de manière sécurisée
            withCredentials([usernamePassword(credentialsId: 'nexus', usernameVariable: 'NEXUS_USER', passwordVariable: 'NEXUS_PASSWORD')]) {
                // Vérification de l'artefact avant le déploiement
                sh 'ls -la target'

                // Crée le fichier settings.xml avec les credentials Nexus
                writeFile file: "$HOME/.m2/settings.xml", text: """
                    <settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
                            xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                            xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd">
                        <servers>
                            <server>
                                <id>nexus</id>
                                <username>${NEXUS_USER}</username>
                                <password>${NEXUS_PASSWORD}</password>
                            </server>
                        </servers>
                    </settings>
                """

                // Déploiement Maven sur Nexus
                sh '''
                    mvn deploy -X \
                        -DaltDeploymentRepository=nexus::default::${NEXUS_URL} \
                        -DskipTests \
                        -s $HOME/.m2/settings.xml
                '''
            }
        }
    }
}


        stage('Archive artifacts') {
            steps {
                echo '📦 Archivage du livrable...'
                sh 'ls -la target'  // Liste les fichiers dans le répertoire target
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
