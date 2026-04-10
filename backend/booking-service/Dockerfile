FROM maven:3.9.9-eclipse-temurin-21 AS common-build
WORKDIR /common
COPY common/pom.xml .
COPY common/src ./src
RUN mvn clean install -DskipTests -q

FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /opt/app
COPY --from=common-build /root/.m2 /root/.m2

COPY booking-service/pom.xml .
RUN mvn dependency:go-offline -B -q

COPY booking-service/src ./src
RUN mvn clean package -DskipTests -q

FROM eclipse-temurin:21-jre
WORKDIR /opt/app
COPY --from=builder /opt/app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
