# Етап 1: Збірка проєкту за допомогою Maven
FROM maven:3.9.6-eclipse-temurin-17-alpine AS build
COPY . .
RUN mvn clean install -DskipTests

# Етап 2: Запуск готового JAR-файлу
FROM eclipse-temurin:17-jre-alpine
COPY --from=build /target/*.jar app.jar
EXPOSE 8080
# Додаємо headless=true, бо на сервері немає монітора для Swing
ENTRYPOINT ["java", "-Djava.awt.headless=true", "-jar", "/app.jar"]