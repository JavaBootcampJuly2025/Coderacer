# Stage 1: Build the React frontend
FROM node:20 AS frontend-build

WORKDIR /frontend

# Install dependencies
COPY frontend/package.json frontend/package-lock.json ./
RUN npm install --legacy-peer-deps

# Copy the rest of the frontend source code
COPY frontend .

# Build the React frontend
RUN npm run build

# Stage 2: Build the Spring Boot application
FROM eclipse-temurin:21-jdk-jammy AS build

WORKDIR /app


# Copy the Maven wrapper and pom.xml to leverage Docker caching
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Grant execute permissions to mvnw
RUN chmod +x mvnw

# Download dependencies (only if pom.xml changes)
RUN ./mvnw dependency:go-offline -B

# Copy the rest of the source code
COPY src ./src

# Copy React build output into Spring Boot static resources
COPY --from=frontend-build /frontend/build ./src/main/resources/static

# Build the Spring Boot application
RUN ./mvnw clean install -DskipTests

# Stage 3: Create the final Docker image
FROM eclipse-temurin:21-jre-jammy

# Set the working directory
WORKDIR /app

# Copy the built JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the port your Spring Boot app runs on (default is 8080, but yours is 8000)
EXPOSE 8000

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
