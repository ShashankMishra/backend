# Dockerfile for Quarkus Java backend (JVM mode)
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY build/quarkus-app/quarkus-run.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]

