FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
COPY . .
RUN mvn clean install -DskipTests

FROM eclipse-temurin:21-jre-alpine

RUN apk add --no-cache tzdata

COPY --from=build /target/*.jar app.jar
EXPOSE 8080

ENTRYPOINT ["java", "-Djava.awt.headless=true", "-Duser.timezone=Europe/Kiev", "-jar", "/app.jar"]