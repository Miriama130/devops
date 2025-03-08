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

        stage('Verify Dockerfile') {
            steps {
                echo 'Verifying Dockerfile...'
                sh 'ls -l' // Vérifiez que le Dockerfile est présent
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

        stage("Déployer l'artefact vers Nexus") {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: 'NEXUS_CREDENTIALS', passwordVariable: 'NEXUS_PASSWORD', usernameVariable: 'NEXUS_USERNAME')]) {
                        sh '''
                            set -e
                            VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)

                            mvn deploy:deploy-file \
                                -Durl=${NEXUS_URL}/repository/${NEXUS_REPO}/ \
                                -DrepositoryId=${NEXUS_REPO} \
                                -Dfile=target/tp-foyer-${VERSION}.jar \
                                -DgroupId=tn.esprit \
                                -DartifactId=tp-foyer \
                                -Dversion=${VERSION} \
                                -Dpackaging=jar \
                                -DgeneratePom=true
                        '''
                    }
                }
            }
        }

        stage('SonarQube Analysis') { // ✅ Déplacé dans `stages {}`
            steps {
                script {
                    withSonarQubeEnv('SonarQube') {
                        withCredentials([string(credentialsId: 'sonar-token', variable: 'SONAR_TOKEN')]) {
                            def sonarCmd = """
                                mvn sonar:sonar \
                                -Dsonar.projectKey=foyer-app \
                                -Dsonar.host.url=http://172.18.64.72:9000 \
                                -Dsonar.login=${SONAR_TOKEN}
                            """.trim()

                            sh sonarCmd
                        }
                    }
                }
            }
        }
    } // ✅ Fin correcte de `stages {}`
} // ✅ Fin correcte de `pipeline {}`
