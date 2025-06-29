# Stage 1: Build the application
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app
COPY . .

RUN chmod +x ./gradlew

RUN ./gradlew build --info

RUN ./gradlew build

# Stage 2: Create the final image
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/build/libs/VirtualBookstore-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]