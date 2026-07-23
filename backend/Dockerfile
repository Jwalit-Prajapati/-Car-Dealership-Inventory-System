# Stage 1: Build the application
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app

# Cache dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Create the runtime image
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copy the built artifact (packaged as .war, but Spring Boot repackages it as an executable archive runnable via `java -jar`)
COPY --from=builder /app/target/*.war app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]