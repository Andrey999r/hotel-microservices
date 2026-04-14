FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /opt/app

COPY pom.xml ./
COPY ./src ./src
RUN mvn package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /opt/app
COPY --from=builder /opt/app/target/*.jar app.jar

ENTRYPOINT ["java","-jar","app.jar"]