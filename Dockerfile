# Multi-stage Dockerfile with separate builds

# ================================
# Main Application Build Stage
# ================================
FROM eclipse-temurin:17-jdk-alpine AS main-builder

WORKDIR /app
COPY pom.xml .
COPY src ./src

# Build only the main application (you might need to adjust this based on your Maven setup)
RUN apt-get update && apt-get install -y maven
RUN mvn clean package -DskipTests


# ================================
# Runner Service Build Stage
# ================================
FROM eclipse-temurin:17-jdk-alpine AS runner-builder

WORKDIR /app
COPY pom.xml .
COPY src ./src

# Build only the runner service (adjust based on your Maven setup)
RUN apt-get update && apt-get install -y maven
RUN mvn clean package -DskipTests


# ================================
# Main Application Runtime Stage
# ================================
FROM eclipse-temurin:17-jdk-alpine AS final-main-app

WORKDIR /app
COPY --from=main-builder /app/target/*.jar app.jar
EXPOSE 8000
ENTRYPOINT ["java", "-jar", "app.jar"]


# ================================
# Runner Service Runtime Stage
# ================================
FROM eclipse-temurin:17-jdk-alpine AS final-runner-service

WORKDIR /app
COPY --from=runner-builder /app/target/*.jar runner.jar
EXPOSE 8001
ENTRYPOINT ["java", "-jar", "runner.jar"]