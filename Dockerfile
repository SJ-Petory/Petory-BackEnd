# 1. 빌드(Build) 단계: Gradle(또는 Maven)을 사용하여 .jar 파일 생성
FROM openjdk:17-jdk-slim as builder
WORKDIR /app
COPY . .
RUN ./gradlew build -x test

# 2. 실행(Run) 단계: 빌드된 .jar 파일만 가져와서 최소한의 환경으로 실행
FROM openjdk:17-jre-slim
WORKDIR /app
# 빌드 단계에서 생성된 .jar 파일을 복사
COPY --from=builder /app/build/libs/*.jar ./app.jar
# 8080 포트 개방
EXPOSE 8080
# 컨테이너 시작 시 실행될 명령어
ENTRYPOINT ["java", "-jar", "app.jar"]