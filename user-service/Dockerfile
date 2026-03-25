FROM eclipse-temurin:21-jdk AS builder

WORKDIR /app

COPY gradlew ./
COPY gradle ./gradle
COPY settings.gradle.kts ./

COPY user-service ./user-service
COPY common-util ./common-util
COPY common-web ./common-web
COPY api-gateway ./api-gateway
COPY orchestrator-worker ./orchestrator-worker
COPY compute-service  ./compute-service

RUN chmod +x ./gradlew

RUN --mount=type=cache,target=/root/.gradle \
    ./gradlew :user-service:bootJar --no-daemon

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=builder /app/user-service/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
