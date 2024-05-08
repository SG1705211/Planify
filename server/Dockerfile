# Use an official Java runtime as a parent image
FROM amazoncorretto:19

# Set the working directory inside the container
WORKDIR /app

# Copy the fat JAR into the container at /app
COPY ./build/libs/KtorServer-all.jar /app/ktor-server.jar
COPY ./src/main/resources/chinook.db /app/chinook.db
COPY ./src/main/resources/test.db /app/test.db

# Make port 8080 available to the world outside this container
EXPOSE 8080

# Run the fat JAR file
CMD ["java", "-jar", "/app/ktor-server.jar"]