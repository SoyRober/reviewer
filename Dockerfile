FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/reviewer-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8003
CMD ["java", "-jar", "app.jar"]