# 빌드 스테이지
FROM openjdk:21-jdk-slim AS builder
WORKDIR /pposonggil

# Gradle Wrapper 복사
COPY gradlew .
COPY gradle gradle
RUN chmod +x ./gradlew

# 의존성 파일 복사 및 다운로드
COPY build.gradle .
COPY settings.gradle .
RUN ./gradlew --no-daemon dependencies

# 소스 코드 복사 및 애플리케이션 빌드
COPY . .
RUN ./gradlew --no-daemon clean build -Pprod

# 실행 스테이지
FROM openjdk:21-jdk-slim
COPY --from=builder /pposonggil/build/libs/*.jar /pposonggil/app.jar
ENTRYPOINT ["java", "-jar", "/pposonggil/app.jar"]