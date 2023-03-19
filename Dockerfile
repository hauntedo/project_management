FROM maven:3.6.0-jdk-8-slim AS build
COPY src /home/application/src
COPY pom.xml /home/application/
USER root
RUN --mount=type=cache,target=/root/.m2 mvn -f /home/application/pom.xml clean compile test install

FROM openjdk:11-jre-slim
COPY --from=build /home/application/target/project-management-0.0.1.jar /usr/local/lib/project-management-0.0.1.jar
ENTRYPOINT ["java", "-jar", "/usr/local/lib/project-management-0.0.1.jar"]
