# Use OpenJDK 21 as the base image
FROM openjdk:21

# Set the working directory inside the container
WORKDIR /app

# Copy the application JAR file into the container
COPY target/orkestria-backend.jar orkestria-backend.jar

# Expose the standard application port
EXPOSE 8080

# Define the command to run the application
ENTRYPOINT ["java", "-jar", "orkestria-backend.jar"]
