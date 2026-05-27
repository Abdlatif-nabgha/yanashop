# Etape 1 : Compilation (Build)
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app

# Optimisation du cache : parent POM d'abord, puis le module
COPY pom.xml .
COPY catalogue-service/pom.xml catalogue-service/
RUN mvn dependency:go-offline -B -pl catalogue-service

# On copie le code source et on compile
COPY catalogue-service/src catalogue-service/src
RUN mvn clean package -DskipTests -pl catalogue-service

# Etape 2 : Execution (Production Image)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Sécurité : Création d'un utilisateur non-root
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# On récupère uniquement le fichier .jar de l'étape 1
COPY --from=builder /app/catalogue-service/target/catalogue-service-*-SNAPSHOT.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]