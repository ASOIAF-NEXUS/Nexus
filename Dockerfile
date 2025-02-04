# This is our interface to AWS SSM Parameter Store
FROM openjdk:17-ea-17-jdk-slim
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} nexus.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/nexus.jar"]