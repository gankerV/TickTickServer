# =============================
# STAGE 1: Build Spring Boot App using Maven
# =============================
FROM maven:3.9.5-eclipse-temurin-17 AS builder
WORKDIR /app

# Copy source code
COPY . /app

# Build project & skip tests (tùy bạn)
RUN mvn clean package -DskipTests

# =============================
# STAGE 2: Run Spring Boot App
# =============================
FROM eclipse-temurin:17-jdk
WORKDIR /app

# Copy JAR file từ builder stage
COPY --from=builder /app/target/*.jar app.jar

# Cổng HTTPS
EXPOSE 8443

# Entry point
ENTRYPOINT ["java", "-jar", "app.jar"]
