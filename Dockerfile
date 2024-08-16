# Użyj obrazu bazowego OpenJDK
FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the Maven wrapper and pom.xml
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Download the dependencies and build the project
RUN ./mvnw dependency:go-offline -B
RUN ./mvnw package -DskipTests

# Copy the built JAR file
COPY target/qr-generator-*.jar app.jar

# Expose the port on which the app runs
EXPOSE 8080

# Command to run the JAR file
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
