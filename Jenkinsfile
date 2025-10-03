// 파이프라인 전체를 관리하는 에이전트로 도커를 사용
agent any

// 환경 변수 설정
environment {
    // Docker Hub 아이디를 여기에 입력하세요
    DOCKERHUB_USERNAME = 'sonii26'
    // Docker Hub에 올릴 이미지 이름을 정합니다
    DOCKER_IMAGE_NAME = "${DOCKERHUB_USERNAME}/petory-backend"
}

// 파이프라인의 각 단계를 정의
stages {
    // 1단계: Git 저장소에서 코드 가져오기 (젠킨스가 자동으로 처리)
    stage('Checkout') {
        steps {
            git branch: 'develop', url: 'https://github.com/SJ-Petory/Petory-BackEnd.git'
        }
    }

    // 2단계: Docker 이미지 빌드하기
    stage('Build') {
        steps {
            echo "===== Start Build Docker Image ====="
            // Dockerfile이 있는 현재 위치에서 이미지를 빌드
            sh "sudo docker build -t ${DOCKER_IMAGE_NAME} ."
            echo "===== Finish Build Docker Image ====="
        }
    }

    // 3단계: Docker Hub에 이미지 업로드하기
    stage('Push to Docker Hub') {
        steps {
            echo "===== Start Push to Docker Hub ====="
            // 위에서 등록한 Docker Hub 자격 증명을 사용하여 로그인하고 이미지를 push
            withCredentials([usernamePassword(credentialsId: 'dockerhub-credentials', passwordVariable: 'DOCKERHUB_PASSWORD', usernameVariable: 'DOCKERHUB_USERNAME')]) {
                sh "sudo docker login -u ${DOCKERHUB_USERNAME} -p ${DOCKERHUB_PASSWORD}"
                sh "sudo docker push ${DOCKER_IMAGE_NAME}"
            }
            echo "===== Finish Push to Docker Hub ====="
        }
    }

    // (다음 단계) 4단계: EC2에 배포하기 (지금은 비워둡니다)
    stage('Deploy') {
        steps {
            echo "Deploy step is not implemented yet."
        }
    }
}