# Dockerfile for Quarkus Java backend (JVM mode)
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY build/quarkus-app/ /app/

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/quarkus-run.jar"]
