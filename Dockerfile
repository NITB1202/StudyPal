# ============ BUILD STAGE ============
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src

#Inject firebase-service-account.json
ARG FIREBASE_JSON_CONTENT_BASE64
RUN mkdir -p src/main/resources/firebase && \
    echo "$FIREBASE_JSON_CONTENT_BASE64" | base64 -d > src/main/resources/firebase/firebase-service-account.json

RUN mvn clean package -DskipTests

# ============ RUNTIME STAGE ============
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]