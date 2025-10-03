// Jenkins Declarative Pipeline for Custom Jenkins Image
pipeline {
    // 모든 작업은 도커 CLI가 내장된 기본 젠킨스 에이전트에서 실행됩니다.
    agent any

    // 파이프라인 전체에서 사용할 환경 변수 설정
    environment {
        DOCKERHUB_USERNAME = 'sonii26'
        DOCKER_IMAGE_NAME = "${DOCKERHUB_USERNAME}/petory-backend"
    }

    // 파이프라인의 각 실행 단계를 정의
    stages {
        // 1단계: Git 저장소에서 코드를 가져오기
        stage('Checkout') {
            steps {
                // 자격 증명을 사용하여 비공개 저장소에 접근합니다.
                git branch: 'develop', url: 'https://github.com/SJ-Petory/Petory-BackEnd.git', credentialsId: 'github-credentials'
            }
        }

        // 2단계: Docker 이미지를 빌드하기
        stage('Build') {
            steps {
                echo "===== Docker 이미지를 빌드합니다 ====="
                // 이제 젠킨스 컨테이너 자체가 docker 명령어를 알고 있으므로 바로 실행합니다.
                // sudo는 필요 없습니다.
                sh "docker build -t ${DOCKER_IMAGE_NAME} ."
                echo "===== 이미지 빌드를 완료했습니다 ====="
            }
        }

        // 3단계: Docker Hub에 이미지 업로드하기
        stage('Push to Docker Hub') {
            steps {
                echo "===== Docker Hub로 이미지를 푸시합니다 ====="
                // 저장된 Docker Hub 자격 증명을 사용하여 로그인하고 푸시합니다.
                withCredentials([usernamePassword(credentialsId: 'dockerhub-credentials', passwordVariable: 'DOCKERHUB_PASSWORD', usernameVariable: 'DOCKERHUB_USERNAME')]) {
                    sh "docker login -u ${DOCKERHUB_USERNAME} -p ${DOCKERHUB_PASSWORD}"
                    sh "docker push ${DOCKER_IMAGE_NAME}"
                }
                echo "===== 이미지 푸시를 완료했습니다 ====="
            }
        }

        // 4단계: EC2에 배포하기 (마지막 단계!)
        stage('Deploy') {
            steps {
                echo "배포 단계는 곧 구현될 예정입니다."
            }
        }
    }
}