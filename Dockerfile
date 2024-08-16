# Użyj obrazu OpenJDK 21 jako bazowego
FROM openjdk:21-jdk-slim AS build

# Ustaw katalog roboczy
WORKDIR /app

# Zainstaluj Maven
RUN apt-get update && apt-get install -y maven

# Skopiuj pliki pom.xml i pobierz zależności
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn
RUN ./mvnw dependency:go-offline -B

# Skopiuj resztę kodu źródłowego
COPY src /app/src

# Zbuduj aplikację
RUN ./mvnw package -DskipTests

# Nowy obraz do uruchomienia aplikacji
FROM openjdk:21-jdk-slim

# Ustaw katalog roboczy
WORKDIR /app

# Skopiuj zbudowany JAR do obrazu
COPY --from=build /app/target/qr-generator-0.0.1-SNAPSHOT.jar app.jar

# Uruchom aplikację
CMD ["java", "-jar", "app.jar"]
