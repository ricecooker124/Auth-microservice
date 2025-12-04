# ---- Build stage ----
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Kopiera Maven wrapper, pom.xml och källkod
COPY . .

# Gör Maven wrapper körbar
RUN chmod +x mvnw

# Bygg Spring Boot-jar, hoppa över tester
RUN ./mvnw -q -DskipTests clean package

# ---- Run stage ----
FROM eclipse-temurin:21-jre
WORKDIR /app

# Kopiera byggd jar från build-steget
COPY --from=build /app/target/*.jar app.jar

# Auth-service lyssnar på 8082
EXPOSE 8082

# SPRING_PROFILES_ACTIVE sätts via docker-compose (docker-profil = application-docker.properties)
ENTRYPOINT ["java","-jar","/app/app.jar"]