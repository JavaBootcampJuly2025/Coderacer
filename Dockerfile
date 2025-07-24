# Multi-stage Dockerfile for Coderacer Application

# ================================
# Build Stage
# ================================
FROM eclipse-temurin:17-jdk AS builder

WORKDIR /app

# Copy Maven files and source code
COPY pom.xml .
COPY src ./src

# Install Maven and build the application (Ubuntu uses apt-get)
RUN apt-get update && apt-get install -y maven
RUN mvn clean package -DskipTests


# ================================
# Main Application Stage
# ================================
FROM eclipse-temurin:17-jre AS final-main-app

WORKDIR /app

# Copy the built JAR file from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Expose port for main app
EXPOSE 8000 3000

# Run the main application
ENTRYPOINT ["java", "-jar", "app.jar"]


# ================================
# Runner Service Stage
# ================================
FROM eclipse-temurin:17-jre AS final-runner-service

WORKDIR /app

# Copy the built JAR file from builder stage
COPY --from=builder /app/target/*.jar runner.jar

# Expose port for runner service
EXPOSE 8001

# Run the runner service
ENTRYPOINT ["java", "-jar", "runner.jar"]