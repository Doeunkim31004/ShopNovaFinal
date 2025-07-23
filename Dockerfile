FROM openjdk:17-jdk-alpine

# 필요한 도구 설치
RUN apk add --no-cache bash curl

# 소스 복사
COPY . /app
WORKDIR /app

# gradlew에 실행 권한 부여
RUN chmod +x ./gradlew

# 빌드
RUN ./gradlew build --no-daemon -x test


# 실행할 JAR 파일 복사
COPY build/libs/shopnova-0.0.1-SNAPSHOT.jar app.jar


# 실행
ENTRYPOINT ["java", "-jar", "/app.jar"]

