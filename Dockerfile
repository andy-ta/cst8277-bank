FROM openjdk:8-jdk-alpine
MAINTAINER Andy Ta
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
WORKDIR /opt/cst8277bank
COPY target/cst8277-bank.jar cst8277.jar
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "cst8277.jar"]
EXPOSE 8080 8443
