# 베이스 이미지: JDK 17
FROM eclipse-temurin:17-jdk-jammy

# 작업 디렉토리 설정
WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

RUN chmod +x ./gradlew && ./gradlew dependencies --no-daemon

#소스 복사
COPY src ./src

# 실제 빌드
RUN ./gradlew bootJar -x test --no-daemon

# 포트 노출
EXPOSE 8080

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "build/libs/Petory-0.0.1-SNAPSHOT.jar"]
