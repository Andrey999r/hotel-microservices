FROM maven:3.9.14-eclipse-temurin-25-alpine AS starter-build
WORKDIR /starter
COPY security-starter/pom.xml .
RUN mvn dependency:go-offline -q
COPY security-starter/src ./src
RUN mvn clean install -DskipTests -q

FROM maven:3.9.14-eclipse-temurin-25-alpine AS common-build
WORKDIR /common
COPY common/pom.xml .
COPY common/src ./src
RUN mvn clean install -DskipTests -q

FROM maven:3.9.14-eclipse-temurin-25-alpine AS builder
WORKDIR /opt/app

COPY --from=starter-build /root/.m2/repository/com/andrey999r/staynova/security-starter /root/.m2/repository/com/andrey999r/staynova/security-starter
COPY --from=common-build /root/.m2 /root/.m2

COPY user-service/pom.xml .
COPY user-service/user-service-domain user-service-domain/
COPY user-service/user-service-application user-service-application/
COPY user-service/user-service-infrastructure user-service-infrastructure/

RUN mvn dependency:go-offline -B -q
RUN mvn clean package -DskipTests -q

FROM eclipse-temurin:25-jre-alpine
WORKDIR /opt/app
COPY --from=builder /opt/app/user-service-infrastructure/target/user-service.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]