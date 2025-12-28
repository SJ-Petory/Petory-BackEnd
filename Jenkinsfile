pipeline {
    agent any

    environment {
        GITHUB_REPO = 'https://github.com/SJ-Petory/Petory-BackEnd.git'
        DOCKER_USERNAME = 'sonii26'
        IMAGE_NAME = "${DOCKER_USERNAME}/petory-backend"
        IMAGE_TAG = "latest"
    }

    stages {
        stage("Check out") {
            steps {
                script {
                    echo "Git Checkout start ---"

                    deleteDir()

                    withCredentials([usernamePassword(credentialsId: 'soni-github', usernameVariable: 'GIT_USER', passwordVariable: 'GIT_TOKEN')]) {

                    echo "Cloning main repository using local key..."
                    sh "git clone -b develop ${GITHUB_REPO} ."

                    sh "git config submodule.\"src/main/resources/config\".url https://${GIT_USER}:${GIT_TOKEN}@github.com/SJ-Petory/config.git"

                    sh "git submodule update --init --recursive"

                    echo "Git Checkout finished ---"
                }
            }
        }
        }
        stage("Build Docker Image") {
            steps {
                script {
                    echo "Build Docker Image start ---"

                    sh "docker build -t ${env.IMAGE_NAME}:${BUILD_NUMBER} ."
                    echo "Build Docker Image finished ---"
                 }
            }
        }
        stage("Push Docker Hub") {
            steps {
                script {
                    echo "Push Docker hub start ---"
                    withCredentials([usernamePassword(credentialsId: 'soni-dockerhub', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASSWORD')]) {
                        sh "echo \$DOCKER_PASSWORD | docker login -u \$DOCKER_USER --password-stdin"
                    }
                    //빌드 번호 태그된 버전 푸시
                    sh "docker push ${env.IMAGE_NAME}:${BUILD_NUMBER}"

                    sh "docker tag ${env.IMAGE_NAME}:${BUILD_NUMBER} ${env.IMAGE_NAME}:${IMAGE_TAG}"
                    sh "docker push ${env.IMAGE_NAME}:${IMAGE_TAG}"
                    echo "Push Docker hub finished ---"
                }
            }
        }
        stage("Deploy") {
                steps {
                    withCredentials([
                        usernamePassword(credentialsId: 'petory-db', usernameVariable: 'DB_USER', passwordVariable: 'DB_PASS'),
                        usernamePassword(credentialsId: 'soni-aws-key', usernameVariable: 'AWS_AK', passwordVariable: 'AWS_SK'),
                        string(credentialsId: 'jwt-secret', variable: 'JWT_SECRET'),
                        string(credentialsId: 'soni-kakao-client-id', variable: 'KAKAO_ID')
                    ]) {
                    script {
                        echo "Deploy start ---"

                        sshPublisher(publishers: [
                            sshPublisherDesc(
                                configName: 'AugustZer0Server', // 젠킨스 시스템 설정에 등록한 서버 이름
                                transfers: [
                                    sshTransfer(
                                        execCommand: """
                            cd /home/augustzer0/soni/petory

                            echo "DB_USERNAME=${DB_USER}" > .env
                            echo "DB_PASSWORD=${DB_PASS}" >> .env
                            echo "AWS_ACCESS_KEY=${AWS_AK}" >> .env
                            echo "AWS_SECRET_KEY=${AWS_SK}" >> .env
                            echo "JWT_SECRET=${JWT_SECRET}" >> .env
                            echo "KAKAO_CLIENT_ID=${KAKAO_ID}" >> .env

                            docker compose pull petory-backend
                            docker compose up -d petory-backend
                        """
                      }
                }
            }
        }
    }
    post {
            always {
                // 파이프라인 종료 후 정리 작업
                script {
                    echo "Pipeline finished. Cleaning up..."
                    // Docker Hub 로그아웃
                    sh 'docker logout'
                    // 빌드에 사용된 로컬 이미지 삭제 (선택 사항)
                    sh "docker rmi ${env.IMAGE_NAME}:${BUILD_NUMBER} || true"
                    sh "docker rmi ${env.IMAGE_NAME}:latest || true"

                    sh "docker image prune -f"
                }
            }
        }
    }