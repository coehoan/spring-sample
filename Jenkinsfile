pipeline {
    agent any

    stages {
        stage('Load .env') {
            steps {
                script {
                    def props = readProperties file: '.env'
                    props.each { key, value ->
                        env."${key}" = value
                    }
                }
            }
        }

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
                sh 'docker build -t ${DOCKER_REPO}:${BUILD_NUMBER} -t ${DOCKER_REPO}:latest .'
            }
        }

        stage('Login & Push') {
            steps {
                withCredentials([usernamePassword(
                        credentialsId: 'coehoan-dockerhub',
                        usernameVariable: 'U',
                        passwordVariable: 'P'
                )]) {
                    sh '''
                        echo "$P" | docker login -u "$U" --password-stdin
                        docker push ${DOCKER_REPO}:${BUILD_NUMBER}
                        docker push ${DOCKER_REPO}:latest
                        docker logout || true
                    '''
                    // --password-stdin: 패스워드 콘솔에 미노출
                }
            }
        }

        stage('Deploy to EC2') {
            steps {
                sh '''
                    set -e
                    docker pull ${DOCKER_REPO}:${BUILD_NUMBER}
                    (docker ps -a -q --filter name=^/${CONTAINER}\\$ | grep -q .) && docker rm -f ${CONTAINER} || true
                    docker run -d --name ${CONTAINER} -p ${APP_PORT}:8080 ${DOCKER_REPO}:${BUILD_NUMBER}
                    docker image prune -f || true
                '''
            }
        }
    }
}