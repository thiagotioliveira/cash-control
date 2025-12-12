FROM amazoncorretto:21-alpine-jdk

WORKDIR /app

COPY target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-Dspring.profiles.active=prod,mock-data", "-jar", "app.jar"]