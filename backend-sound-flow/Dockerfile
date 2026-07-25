# 1. faza - Build aplikacije
FROM maven:3.9.3-eclipse-temurin-17 as builder
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src

RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app
# Port kao argument (opcionalno)
ARG DEFAULT_PORT=8080
ENV PORT=$DEFAULT_PORT

# Izloženi port (nije obavezan ali je dobra praksa)
EXPOSE $PORT

# Kopiranje JAR-a iz builder stage-a
COPY --from=builder /app/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]