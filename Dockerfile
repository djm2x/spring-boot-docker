# FROM openjdk:16-alpine3.13
# FROM openjdk:17-jdk-alpine3.14
FROM adoptopenjdk/openjdk16:alpine-jre

EXPOSE 8082
EXPOSE 443

ARG JAR_FILE=target/first-0.0.1-SNAPSHOT.jar

WORKDIR /opt/app

COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java","-jar","app.jar"]