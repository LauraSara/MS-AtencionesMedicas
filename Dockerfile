FROM openjdk:21-jdk-slim
WORKDIR /app
COPY target/MS-ATENCIONESMEDICAS-1.0.0.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]