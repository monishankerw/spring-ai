pipeline {
    agent any

    stages {

        stage('Checkout') {
            steps {
                echo 'Checking out spring-ai project'
                checkout scm
            }
        }

        stage('Build') {
            steps {
                echo 'Building Spring Boot application'
                sh './mvnw clean package -DskipTests'
            }
        }

        stage('Test') {
            steps {
                echo 'Running tests'
                sh './mvnw test'
            }
        }

        stage('Archive') {
            steps {
                archiveArtifacts artifacts: 'target/*.jar',
                    fingerprint: true
            }
        }
    }

    post {
        success {
            echo 'Spring AI build successful!'
        }

        failure {
            echo 'Spring AI build failed!'
        }
    }
}