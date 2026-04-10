FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /opt/app

COPY mvnw pom.xml ./
COPY ./src ./src
RUN mvn clean install -DskipTests

COPY src ./src
RUN mvn package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /opt/app
COPY --from=builder /opt/app/target/*.jar /opt/app/*.jar

ENTRYPOINT ["java","-jar","/opt/app/*.jar"]