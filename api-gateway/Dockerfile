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

RUN chmod +x ./gradlew \
  &&  --mount=type=cache,target=/root/.gradle \
    ./gradlew :api-gateway:bootJar --no-daemon

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=builder /app/api-gateway/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
