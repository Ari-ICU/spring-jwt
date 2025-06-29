# Use multi-arch compatible OpenJDK 17 image
FROM eclipse-temurin:17-jdk

# Set working directory inside the container
WORKDIR /app

# Copy the jar file built by IntelliJ/Gradle
COPY build/libs/VirtualBookstore-0.0.1-SNAPSHOT.jar app.jar

# Expose Spring Boot default port
EXPOSE 8000

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
