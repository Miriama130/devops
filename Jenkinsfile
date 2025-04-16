// pipeline {
//     agent any

//     environment {
//         DOCKER_IMAGE = "onsdachraoui/foyer-app:latest"
//         NEXUS_REPO = "maven-releases"  // Remplace par "maven-snapshots" si nécessaire
//         NEXUS_URL = 'http://172.18.64.72:8081/repository/maven-snapshots'
//     }

//     stages {
//         stage('Clone Repository') {
//             steps {
//                 script {
//                     echo 'Cloning Repository...'
//                     checkout([
//                         $class: 'GitSCM',
//                         branches: [[name: 'ons']],
//                         userRemoteConfigs: [[
//                             url: 'https://github.com/Miriama130/devops.git',
//                             credentialsId: 'nsgit'
//                         ]]
//                     ])
//                 }
//             }
//         }

//         stage('Setup Maven') {
//             steps {
//                 echo 'Checking Maven version...'
//                 sh 'mvn --version'
//             }
//         }

//         stage('Maven Build') {
//             steps {
//                 echo 'Building Maven Project...'
//                 sh 'mvn clean package'
//                 sh 'ls -l target/'
//             }
//         }

//         stage('Run Unit Tests') {
//             steps {
//                 echo 'Running Unit Tests...'
//                 sh 'mvn test'
//             }
//         }

//         stage('Verify Dockerfile') {
//             steps {
//                 echo 'Checking Dockerfile presence...'
//                 sh '''
//                 if [ ! -f Dockerfile ]; then
//                     echo "ERROR: Dockerfile is missing!"
//                     exit 1
//                 fi
//                 ls -l Dockerfile
//                 '''
//             }
//         }

//         stage('Build Docker Image') {
//             steps {
//                 script {
//                     withCredentials([usernamePassword(credentialsId: 'DOCKER', passwordVariable: 'DOCKER_PASSWORD', usernameVariable: 'DOCKER_USERNAME')]) {
//                         sh '''
//                         echo $DOCKER_PASSWORD | docker login -u $DOCKER_USERNAME --password-stdin
//                         docker build -t $DOCKER_IMAGE .
//                         '''
//                     }
//                 }
//             }
//         }

//         // stage('Push Docker Image') {
//         //     steps {
//         //         script {
//         //             withCredentials([usernamePassword(credentialsId: 'DOCKER', passwordVariable: 'DOCKER_PASSWORD', usernameVariable: 'DOCKER_USERNAME')]) {
//         //                 sh '''
//         //                 echo $DOCKER_PASSWORD | docker login -u $DOCKER_USERNAME --password-stdin
//         //                 docker push $DOCKER_IMAGE
//         //                 '''
//         //             }
//         //         }
//         //     }
//         // }

//         stage('Run Tests with Spring Profile') {
//             steps {
//                 echo 'Running Tests with Spring Profile...'
//                 sh 'mvn test -Dspring.profiles.active=test'
//             }
//         }

//         stage('Deploy to Nexus') {
//             steps {
//                 withCredentials([usernamePassword(credentialsId: 'nex-cred', usernameVariable: 'NEXUS_USER', passwordVariable: 'NEXUS_PASS')]) {
//                     sh '''
//                     mvn deploy \
//                         -DrepositoryId=nexus \
//                         -Durl=$NEXUS_URL \
//                         -Dusername=$NEXUS_USER \
//                         -Dpassword=$NEXUS_PASS
//                     '''
//                 }
//             }
//         }

//         stage('SonarQube Analysis') {
//             steps {
//                 script {
//                     withSonarQubeEnv('SonarQube') {
//                         withCredentials([string(credentialsId: 'JENKINS-SONAR', variable: 'SONAR_TOKEN')]) {
//                             sh '''
//                             mvn sonar:sonar \
//                                 -Dsonar.projectKey=foyer-app \
//                                 -Dsonar.host.url=http://172.18.64.72:9000 \
//                                 -Dsonar.login=$SONAR_TOKEN
//                             '''
//                         }
//                     }
//                 }
//             }
//         }
//     }

//     post {
//         always {
//             echo "Pipeline completed!"
//         }
//         failure {
//             echo "Pipeline failed!"
//         }
//         success {
//             echo "Pipeline succeeded!"
//         }
//     }
// }


pipeline {
    agent any

    environment {
        DOCKER_IMAGE = "onsdachraoui/foyer-app"
        NEXUS_REPO = "maven-snapshots"
        NEXUS_URL = 'http://172.18.64.72:8081/repository/maven-snapshots'
        // Automatically get version from pom.xml
        POM_VERSION = readMavenPom().getVersion() 
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
                            credentialsId: 'nsgit'
                        ]]
                    ])
                }
            }
        }

        stage('Setup Environment') {
            steps {
                echo 'Checking Tools...'
                sh 'mvn --version'
                sh 'docker --version'
                sh 'java -version'
            }
        }

        stage('Maven Build') {
            steps {
                echo 'Building Maven Project...'
                sh 'mvn clean package -DskipTests'
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }

        stage('Run Tests') {
            parallel {
                stage('Unit Tests') {
                    steps {
                        echo 'Running Unit Tests...'
                        sh 'mvn test'
                    }
                }
                stage('Integration Tests') {
                    steps {
                        echo 'Running Integration Tests...'
                        sh 'mvn verify -DskipUnitTests'
                    }
                }
            }
        }

        stage('Verify Docker Setup') {
            steps {
                echo 'Checking Docker Configuration...'
                sh '''
                if [ ! -f Dockerfile ]; then
                    echo "ERROR: Dockerfile is missing!"
                    exit 1
                fi
                ls -l Dockerfile
                cat Dockerfile
                '''
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    withCredentials([usernamePassword(
                        credentialsId: 'DOCKER', 
                        passwordVariable: 'DOCKER_PASSWORD', 
                        usernameVariable: 'DOCKER_USERNAME'
                    )]) {
                        sh """
                        echo "\$DOCKER_PASSWORD" | docker login -u "\$DOCKER_USERNAME" --password-stdin
                        docker build -t ${DOCKER_IMAGE}:${POM_VERSION} -t ${DOCKER_IMAGE}:latest .
                        """
                    }
                }
            }
        }

        stage('Push Docker Image') {
            steps {
                script {
                    withCredentials([usernamePassword(
                        credentialsId: 'DOCKER', 
                        passwordVariable: 'DOCKER_PASSWORD', 
                        usernameVariable: 'DOCKER_USERNAME'
                    )]) {
                        sh """
                        echo "\$DOCKER_PASSWORD" | docker login -u "\$DOCKER_USERNAME" --password-stdin
                        docker push ${DOCKER_IMAGE}:${POM_VERSION}
                        docker push ${DOCKER_IMAGE}:latest
                        """
                    }
                }
            }
        }

        stage('Deploy to Nexus') {
            steps {
                script {
                    try {
                        withCredentials([usernamePassword(
                            credentialsId: 'nex-cred', 
                            usernameVariable: 'NEXUS_USER', 
                            passwordVariable: 'NEXUS_PASS'
                        )]) {
                            sh """
                            mvn deploy \
                                -DrepositoryId=nexus \
                                -Durl=${NEXUS_URL} \
                                -Dusername=${NEXUS_USER} \
                                -Dpassword=${NEXUS_PASS} \
                                -DskipTests=true
                            """
                        }
                    } catch (e) {
                        echo "Deployment failed: ${e}"
                        currentBuild.result = 'UNSTABLE'
                    }
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                script {
                    withSonarQubeEnv('SonarQube') {
                        withCredentials([string(
                            credentialsId: 'JENKINS-SONAR', 
                            variable: 'SONAR_TOKEN'
                        )]) {
                            sh """
                            mvn sonar:sonar \
                                -Dsonar.projectKey=foyer-app \
                                -Dsonar.host.url=http://172.18.64.72:9000 \
                                -Dsonar.login=${SONAR_TOKEN} \
                                -Dsonar.projectVersion=${POM_VERSION}
                            """
                        }
                    }
                }
            }
        }

        stage('Quality Gate') {
            steps {
                script {
                    timeout(time: 1, unit: 'HOURS') {
                        waitForQualityGate abortPipeline: true
                    }
                }
            }
        }
    }

    post {
        always {
            echo "Pipeline execution completed"
            cleanWs()
        }
        success {
            echo "Pipeline succeeded!"
            mail to: 'team@example.com',
                 subject: "Pipeline Success: ${env.JOB_NAME}",
                 body: "Build ${env.BUILD_NUMBER} completed successfully."
        }
        failure {
            echo "Pipeline failed!"
            mail to: 'team@example.com',
                 subject: "Pipeline Failed: ${env.JOB_NAME}",
                 body: "Build ${env.BUILD_NUMBER} failed. Please check: ${env.BUILD_URL}"
        }
        unstable {
            echo "Pipeline unstable!"
        }
    }
}
