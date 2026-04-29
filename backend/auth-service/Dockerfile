FROM maven:3.9.14-eclipse-temurin-25-alpine AS starter-build
WORKDIR /starter
COPY security-starter/pom.xml .
RUN mvn dependency:go-offline -q
COPY security-starter/src ./src
RUN mvn clean install -DskipTests -q

FROM maven:3.9.14-eclipse-temurin-25-alpine AS build
WORKDIR /opt/app

COPY --from=starter-build /root/.m2/repository/com/andrey999r/staynova/security-starter /root/.m2/repository/com/andrey999r/staynova/security-starter
COPY auth-service/pom.xml .

RUN mvn dependency:go-offline -B

COPY auth-service/src src
RUN mvn package -DskipTests -B

FROM eclipse-temurin:25-jre-alpine
WORKDIR /opt/app

COPY --from=build /opt/app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]