FROM maven:3.9.9-eclipse-temurin-24-alpine AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -q

COPY src src
RUN mvn clean install -DskipTests -q

FROM scratch
COPY --from=build /root/.m2/repository/com/andrey999r/staynova/security-starter /security-starter
