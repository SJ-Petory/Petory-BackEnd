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

                    withCredentials([usernamePassword(credentialsId: 'github-credentials', usernameVariable: 'GIT_USER', passwordVariable: 'GIT_TOKEN')]) {

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
                    withCredentials([usernamePassword(credentialsId: 'dockerhub-credentials', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASSWORD')]) {
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
                        string(credentialsId: 'db-config', variable: 'DB_CONFIG_FILE'),
                        string(credentialsId: 'aws-s3-key', variable: 'AWS_KEY_FILE'),
                        string(credentialsId: 'jwt-secret', variable: 'JWT_SECRET_ENV'),
                        string(credentialsId: 'kakao-client-id', variable: 'KAKAO_CLIENT_ID_ENV')
                    ]) {
                    script {
                        def db = readJSON text: DB_CONFIG_FILE
                        def aws = readJSON text: AWS_KEY_FILE

                            sh """
                                cd /home/ec2-user/petory

                                echo "IMAGE_TAG=${env.IMAGE_NAME}:latest" > .env
                                echo "DB_HOST=${db.DB_HOST}" >> .env
                                echo "DB_PORT=${db.DB_PORT}" >> .env
                                echo "DB_NAME=${db.DB_NAME}" >> .env
                                echo "DB_USERNAME=${db.DB_USERNAME}" >> .env
                                echo "DB_PASSWORD=${db.DB_PASSWORD}" >> .env
                                echo "AWS_ACCESS_KEY=${aws.ACK}" >> .env
                                echo "AWS_SECRET_KEY=${aws.SCK}" >> .env
                                echo "JWT_SECRET=${JWT_SECRET_ENV}" >> .env
                                echo "KAKAO_CLIENT_ID=${KAKAO_CLIENT_ID_ENV}" >> .env

                                IMAGE_TAG=${env.IMAGE_NAME}:latest docker compose pull
                                IMAGE_TAG=${env.IMAGE_NAME}:latest docker compose up -d

                                # docker pull ${env.IMAGE_NAME}:latest
                                # docker compose up -d

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