pipeline {
    agent any

    environment {
        // Nexus Configuration (UPDATE THESE)
        NEXUS_URL = "http://your-nexus-server:8081"  // e.g., http://nexus.example.com:8081
        NEXUS_REPO = "maven-releases"                // Your Maven repository (e.g., 'maven-releases' or 'maven-snapshots')
        NEXUS_CREDS = credentials('nexus-credentials') // Jenkins credentials ID for Nexus (username + password)

        // SonarQube (Optional)
        SONAR_HOST_URL = "http://172.17.102.63:9000"
        SONAR_PROJECT_KEY = "devops"
        SONAR_PROJECT_NAME = "devops"
    }

    stages {
        stage('Checkout') {
            steps { checkout scm }
        }

        stage('Build & Test') {
            steps {
                sh 'mvn clean verify'
            }
            post {
                always {
                    junit 'target/surefire-reports/**/*.xml'
                    archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('Sonar') {
                    withCredentials([string(credentialsId: 'devopes', variable: 'SONAR_TOKEN')]) {
                        sh """
                            mvn sonar:sonar \
                            -Dsonar.projectKey=${env.SONAR_PROJECT_KEY} \
                            -Dsonar.projectName=${env.SONAR_PROJECT_NAME} \
                            -Dsonar.host.url=${env.SONAR_HOST_URL} \
                            -Dsonar.login=${SONAR_TOKEN} \
                            -Dsonar.java.binaries=target/classes \
                            -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml
                        """
                    }
                }
            }
        }

        stage('Deploy to Nexus') {
            when { expression { currentBuild.resultIsBetterOrEqualTo('SUCCESS') } }
            steps {
                script {
                    // Deploy JAR + POM to Nexus using Maven
                    sh """
                        mvn deploy:deploy-file \
                        -DgroupId=\$(mvn help:evaluate -Dexpression=project.groupId -q -DforceStdout) \
                        -DartifactId=\$(mvn help:evaluate -Dexpression=project.artifactId -q -DforceStdout) \
                        -Dversion=\$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout) \
                        -Dpackaging=jar \
                        -Dfile=target/\$(mvn help:evaluate -Dexpression=project.build.finalName -q -DforceStdout).jar \
                        -DpomFile=pom.xml \
                        -DrepositoryId=nexus \
                        -Durl=${env.NEXUS_URL}/repository/${env.NEXUS_REPO} \
                        -s settings.xml
                    """
                    echo "✅ Successfully deployed to Nexus!"
                }
            }
        }
    }

    post {
        always {
            echo "Pipeline completed - Cleaning up..."
        }
        success {
            echo "✅ Pipeline succeeded! Artifact deployed to Nexus."
        }
        failure {
            echo "❌ Pipeline failed! Check logs for errors."
        }
    }
}