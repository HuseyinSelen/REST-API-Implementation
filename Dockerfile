# 1. Build stage
FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /app

COPY . .
COPY settings.xml /root/.m2/settings.xml

WORKDIR /app/package-manager-app
RUN mvn clean package spring-boot:repackage -DskipTests

# 2. Runtime stage
FROM eclipse-temurin:17-jdk
WORKDIR /app

COPY --from=build /app/package-manager-app/target/package-manager-app-1.0.1.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
