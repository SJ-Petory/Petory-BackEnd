# 베이스 이미지: JDK 17
FROM eclipse-temurin:17-jdk-jammy

# 작업 디렉토리 설정
WORKDIR /app

# 프로젝트 전체 소스코드를 이미지 안으로 복사
COPY . .

# gradlew 실행 권한 부여
RUN chmod +x ./gradlew

# Dockerfile 내부에서  Gradle 사용해서 프로젝트 빌드하고 .jar 파일 생성
RUN ./gradlew bootJar -x test

# 포트 노출
EXPOSE 8080

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "build/libs/Petory-0.0.1-SNAPSHOT.jar"]
