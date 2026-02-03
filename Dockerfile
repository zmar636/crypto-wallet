# -------------------------------
# Stage 1: Build the JAR
# -------------------------------
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

# Copy Maven wrapper, pom.xml, and .mvn folder
COPY mvnw .
COPY pom.xml .
COPY .mvn .mvn

# Make Maven wrapper executable
RUN chmod +x mvnw

# Copy source code
COPY src ./src

# Build the project (skip tests)
RUN ./mvnw clean package -DskipTests -B

# -------------------------------
# Stage 2: Runtime
# -------------------------------
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy JAR from build stage
COPY --from=build /app/target/cryptowallet-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
