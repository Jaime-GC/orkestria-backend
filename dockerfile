# Build Stage
FROM maven:3.9.6-eclipse-temurin-21 AS builder

# Set working directory
WORKDIR /app

# Copy the entire backend project
COPY . .

# Compile the project and skip tests
RUN mvn clean package -DskipTests




# Runtime Stage
# Use OpenJDK 21 as the base image
FROM openjdk:21

# Set the working directory inside the container
WORKDIR /app

# Copy the application JAR file from the builder stage
COPY --from=builder /app/target/orkestria-backend-1.0.0.jar orkestria-backend.jar

# Expose the standard application port
EXPOSE 8080

# Define the command to run the application
ENTRYPOINT ["java", "-jar", "orkestria-backend.jar"]
