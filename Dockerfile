FROM openjdk:17-jdk-slim
ARG JAR_FILE=target/rest.api-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} api_rest_springboot_react.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "api_rest_springboot_react.jar"]