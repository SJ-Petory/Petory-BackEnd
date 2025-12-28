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

                    // 👇 [수정] 스크린샷에 있던 ID 'github-login'으로 맞췄습니다!
                    withCredentials([usernamePassword(credentialsId: 'github-login', usernameVariable: 'GIT_USER', passwordVariable: 'GIT_TOKEN')]) {

                        echo "Cloning main repository..."
                        sh "git clone -b develop ${GITHUB_REPO} ."

                        // 서브모듈 설정
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
                    // Dockerfile 내부에서 빌드 수행
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

                        // 빌드 번호 버전과 latest 버전 둘 다 푸시
                        sh "docker push ${env.IMAGE_NAME}:${BUILD_NUMBER}"
                        sh "docker tag ${env.IMAGE_NAME}:${BUILD_NUMBER} ${env.IMAGE_NAME}:${IMAGE_TAG}"
                        sh "docker push ${env.IMAGE_NAME}:${IMAGE_TAG}"
                    }
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

                        // 👇 [수정] 괄호 닫힘 오류 해결 완료!
                        sshPublisher(publishers: [
                            sshPublisherDesc(
                                configName: 'AugustZer0Server',
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
                                    )
                                ]
                            )
                        ])
                    }
                }
            }
        }
    }
    
    post {
        always {
            script {
                echo "Pipeline finished. Cleaning up..."
                sh 'docker logout'
                sh "docker rmi ${env.IMAGE_NAME}:${BUILD_NUMBER} || true"
                sh "docker rmi ${env.IMAGE_NAME}:latest || true"
                sh "docker image prune -f"
            }
        }
    }
}