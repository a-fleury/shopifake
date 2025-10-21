# Stage 1: Build
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copier les fichiers de configuration Maven
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copier le code source
COPY src ./src

# Build l'application
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copier le JAR depuis le stage de build
COPY --from=build /app/target/*.jar app.jar

# Exposer le port
EXPOSE 8081

# Variables d'environnement par défaut
ENV KEYCLOAK_SERVER_URL=http://keycloak:8080
ENV KEYCLOAK_REALM=shopifake
ENV KEYCLOAK_CLIENT_ID=shopifake-client
ENV KEYCLOAK_CLIENT_SECRET=your-client-secret
ENV KEYCLOAK_ADMIN_USERNAME=admin
ENV KEYCLOAK_ADMIN_PASSWORD=admin

# Lancer l'application
ENTRYPOINT ["java", "-jar", "app.jar"]
