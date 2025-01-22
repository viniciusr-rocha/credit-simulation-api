FROM eclipse-temurin:21-jdk-alpine AS jre-builder

WORKDIR /deployments
COPY build/libs/credit-simulation-api-0.0.1.jar ./credit-simulation-api.jar

EXPOSE 8080
ENTRYPOINT java -jar ./credit-simulation-api.jar