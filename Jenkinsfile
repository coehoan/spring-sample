pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                git url: 'https://github.com/coehoan/spring-sample.git', branch: 'master'
            }
        }

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
                        usernameVariable: 'username',
                        passwordVariable: 'password'
                )]) {
                    sh '''
                        set -e
                        echo "$password" | docker login -u "$username" --password-stdin
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
                   
                    export IMAGE_TAG=${BUILD_NUMBER}
                    docker compose up -d --no-deps --force-recreate app
                    
                    docker exec ${NGINX_CONTAINER} nginx -t && docker exec ${NGINX_CONTAINER} nginx -s reload || docker compose up -d nginx
                    
                    docker image prune -f || true
                    docker images ${DOCKER_REPO} --format {{.Repository}}:{{.Tag}} | grep -v ':latest' | xargs -r docker rmi -f
                '''
            }
        }
    }
}