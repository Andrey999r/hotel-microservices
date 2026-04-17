FROM maven:3.9.14-eclipse-temurin-25-alpine AS builder
WORKDIR /opt/app
COPY pom.xml .
RUN mvn dependency:go-offline -B -q
COPY src ./src
RUN mvn clean package -DskipTests -q

FROM eclipse-temurin:25-jre-alpine
WORKDIR /opt/app
COPY --from=builder /opt/app/target/config-server.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
