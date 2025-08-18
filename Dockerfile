FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8002
CMD ["java", "-jar", "app.jar"]