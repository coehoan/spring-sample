pipeline {
    agent any

    environment {
        IMAGE = 'cms-app:latest'
        CONTAINER = 'cms-test'
        APP_PORT = '8080'
    }

    stages {
        stage('Checkout') {
            steps {
                git url: 'https://github.com/coehoan/spring-sample.git', branch: 'master'
            }
        }

        stage('Build') {
            steps {
                sh 'chmod +x gradlew'
                sh './gradlew clean build -x test'
            }
        }

        stage('Docker Build') {
            steps {
                sh 'docker build -t ${IMAGE} .'
            }
        }
    }
}