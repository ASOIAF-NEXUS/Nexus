# This is our interface to AWS SSM Parameter Store
FROM openjdk:23-ea-23-jdk-slim AS build
COPY . /
RUN ./gradlew build

FROM openjdk:23-ea-23-jdk-slim AS app
COPY --from=BUILD /build/libs/*.jar asoiaf-nexus.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/asoiaf-nexus.jar"]
