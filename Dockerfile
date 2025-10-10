# =================================================================
# 1. 빌드(Build) 단계: 소스 코드를 컴파일하고 실행 가능한 JAR 파일을 만듭니다.
# =================================================================
FROM eclipse-temurin:17-jdk-jammy AS builder

# 작업 디렉토리 설정
WORKDIR /workspace/app

# Gradle 관련 파일들을 먼저 복사하여 Docker의 레이어 캐시를 활용합니다.
# 이렇게 하면 의존성이 변경되지 않았을 때 빌드 속도가 향상됩니다.
COPY gradlew .
COPY gradle ./gradle
COPY build.gradle .
COPY settings.gradle .

# gradlew 실행 권한 부여
RUN chmod +x ./gradlew

# 소스 코드를 복사하기 전에 의존성을 먼저 다운로드하여 캐시 효과를 극대화합니다.
RUN ./gradlew dependencies

# 프로젝트의 모든 소스 코드(서브모듈 포함)를 복사합니다.
# Gradle이 빌드 시점에 서브모듈의 리소스(설정 파일 등)를 올바르게 포함하도록 합니다.
COPY . .

RUN chmod +x ./gradlew
# 테스트를 제외하고 애플리케이션을 빌드하여 실행 가능한 JAR 파일을 생성합니다.
RUN ./gradlew bootJar -x test

# =================================================================
# 2. 실행(Run) 단계: 빌드된 JAR 파일을 실행하는 가벼운 이미지를 만듭니다.
# =================================================================
FROM eclipse-temurin:17-jre-jammy

# 작업 디렉토리 설정
WORKDIR /app

# 빌드 단계(builder)에서 생성된 JAR 파일만 최종 이미지로 복사합니다.
# 이를 통해 JDK, 소스코드, 빌드 도구 등이 포함되지 않은 가벼운 이미지가 만들어집니다.
COPY --from=builder /workspace/app/build/libs/*.jar app.jar

# 애플리케이션이 사용할 포트를 외부에 노출합니다.
EXPOSE 8080

# 컨테이너가 시작될 때 애플리케이션 JAR 파일을 실행합니다.
ENTRYPOINT ["java", "-jar", "app.jar"]
