FROM openjdk:21-jdk-slim AS build

WORKDIR /app

RUN apt-get update && apt-get install -y maven

COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn
RUN ./mvnw dependency:go-offline -B

COPY src /app/src

RUN ./mvnw package -DskipTests

FROM openjdk:21-jdk-slim

WORKDIR /app

COPY --from=build /app/target/qr-generator-0.0.1-SNAPSHOT.jar app.jar

CMD ["java", "-jar", "app.jar"]
