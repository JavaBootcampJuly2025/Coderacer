# Multi-stage Dockerfile for Coderacer Application with React Frontend

# ================================
# React Build Stage
# ================================
FROM node:18-alpine AS react-builder

WORKDIR /app/frontend

# Copy React files
COPY frontend/package*.json ./
RUN npm ci

# Copy React source code and build
COPY frontend/. .
RUN npm run build


# ================================
# Java Build Stage
# ================================
FROM eclipse-temurin:17-jdk AS java-builder

WORKDIR /app

# Copy Maven files and source code
COPY pom.xml .
COPY src ./src

# Install Maven and build the application
RUN apt-get update && apt-get install -y maven
RUN mvn clean package -DskipTests


# ================================
# Main Application Stage (Spring Boot + React)
# ================================
FROM eclipse-temurin:17-jre AS final-main-app

WORKDIR /app

# Copy the built JAR file from Java builder stage
COPY --from=java-builder /app/target/*.jar app.jar

# Copy built React frontend files (if serving from Spring Boot)
# Uncomment these lines if you want to serve React from Spring Boot
# COPY --from=react-builder /app/frontend/build ./static

# Expose ports for main app (backend) and React frontend
EXPOSE 8000 3000

# Run the main application
ENTRYPOINT ["java", "-jar", "app.jar"]