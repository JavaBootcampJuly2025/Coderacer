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

# Stage 2: Build the Main Spring Boot application
FROM eclipse-temurin:21-jdk-jammy AS main-app-build

WORKDIR /app

# Copy the Maven wrapper and root pom.xml
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Grant execute permissions to mvnw
RUN sed -i 's/\r$//' mvnw
RUN chmod +x mvnw

# Download dependencies (only if pom.xml changes)
# This helps with Docker layer caching for faster subsequent builds
RUN ./mvnw dependency:go-offline -B

# Copy the main application's source code
COPY src ./src

# Copy React build output into Spring Boot static resources
COPY --from=frontend-build /frontend/build ./src/main/resources/static

# Build the Main Spring Boot application
RUN ./mvnw clean install -DskipTests

# Stage 3: Build the Coderacer.Runner microservice
FROM eclipse-temurin:17-jdk-jammy AS runner-build

# Set the working directory inside the container for building
WORKDIR /build

# Copy the Maven wrapper, .mvn directory, and the root pom.xml
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Grant execute permissions to mvnw
RUN sed -i 's/\r$//' mvnw
RUN chmod +x mvnw

# Copy the specific 'runner' module's source code and its pom.xml
# The path 'runner' corresponds to your module directory name.
COPY runner ./runner

# Build the specific Spring Boot application module
# -pl runner: Specifies the 'runner' module to build.
# -am: Also builds required modules.
# -DskipTests: Skips tests for a faster image build.
RUN ./mvnw clean install -pl runner -am -DskipTests

# Stage 4: Create the final Docker image for the Main Coderacer App
FROM eclipse-temurin:21-jre-jammy AS final-main-app

# Set the working directory
WORKDIR /app

# Copy the built JAR from the main-app-build stage
COPY --from=main-app-build /app/target/*.jar app.jar

# Expose the port your Main Coderacer App runs on
EXPOSE 8000

# Run the Main Coderacer application
ENTRYPOINT ["java", "-jar", "app.jar"]

# Stage 5: Create the final Docker image for the Coderacer.Runner Microservice
FROM eclipse-temurin:17-jre-jammy AS final-runner-service

# Set the working directory
WORKDIR /app

# Copy the built JAR from the runner-build stage for the specific module
# The JAR will be located in 'runner/target/' within the build context.
COPY --from=runner-build /build/runner/target/*.jar app.jar

# Expose the port your Coderacer.Runner app runs on
EXPOSE 8001

# Define the command to run the Coderacer.Runner application
ENTRYPOINT ["java", "-jar", "app.jar", "--server.port=8001"]