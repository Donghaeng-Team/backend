# Multi-stage build for API Gateway
FROM gradle:8.5-jdk17 AS build

WORKDIR /app
COPY build.gradle settings.gradle ./
COPY src ./src

# Build the application
RUN gradle build -x test --no-daemon

FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Copy the built jar file
COPY --from=build /app/build/libs/*.jar app.jar

# Create non-root user
RUN groupadd -r apigateway && useradd -r -g apigateway apigateway

# Install curl for health check
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

USER apigateway

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]