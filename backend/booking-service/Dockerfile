FROM maven:3.9.14-eclipse-temurin-25-alpine AS security-starter-build
WORKDIR /starter
COPY security-starter/pom.xml .
COPY security-starter/src ./src
RUN mvn clean install -DskipTests -q

FROM maven:3.9.14-eclipse-temurin-25-alpine AS common-build
WORKDIR /common
COPY common/pom.xml .
COPY common/src ./src
RUN mvn clean install -DskipTests -q

FROM maven:3.9.14-eclipse-temurin-25-alpine AS builder
WORKDIR /opt/app
COPY --from=security-starter-build /root/.m2/repository/com/andrey999r/staynova/security-starter /root/.m2/repository/com/andrey999r/staynova/security-starter
COPY --from=common-build /root/.m2 /root/.m2

COPY booking-service/pom.xml .
RUN mvn dependency:go-offline -B -q

COPY booking-service/src ./src
RUN mvn clean package -DskipTests -q

FROM eclipse-temurin:25-jdk-alpine
WORKDIR /opt/app
COPY --from=builder /opt/app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
