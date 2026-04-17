FROM maven:3.9.14-eclipse-temurin-25-alpine AS builder
WORKDIR /opt/app

COPY pom.xml ./
COPY ./src ./src
RUN mvn package -DskipTests

FROM eclipse-temurin:25-jre-alpine
WORKDIR /opt/app
COPY --from=builder /opt/app/target/*.jar app.jar

ENTRYPOINT ["java","-jar","app.jar"]