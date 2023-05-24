FROM maven:3.8.1-openjdk-17-slim as build
ENV HOME=/usr/app
RUN mkdir -p $HOME
WORKDIR $HOME
#COPY .mvn .mvn
COPY mvnw .
COPY pom.xml .
COPY src src
RUN --mount=type=cache,target=/root/.m2 mvn -f $HOME/pom.xml clean package

FROM openjdk:19-slim-buster
COPY --from=build /usr/app/target/*.jar /app/monolith.jar
ENTRYPOINT java -jar /app/monolith.jar
