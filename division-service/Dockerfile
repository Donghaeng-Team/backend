# Multi-stage build for Division Service
FROM gradle:8.5-jdk17 AS build
USER gradle
WORKDIR /app
COPY --chown=gradle:gradle build.gradle settings.gradle ./
COPY --chown=gradle:gradle src ./src

# Build the application
RUN gradle build -x test --no-daemon

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Create non-root user
RUN groupadd -r divisionservice && useradd -r -g divisionservice divisionservice

# Copy the built jar file
COPY --from=build /app/build/libs/*.jar app.jar
RUN chown divisionservice:divisionservice /app/app.jar

USER divisionservice

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8081/actuator/health || exit 1

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "/app/app.jar"]

