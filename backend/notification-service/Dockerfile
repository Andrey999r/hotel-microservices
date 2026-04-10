FROM maven:3.9.9-eclipse-temurin-21 AS common-build
WORKDIR /common
COPY common/pom.xml .
COPY common/src ./src
RUN mvn clean install -DskipTests -q

FROM gradle:8.13-jdk21 AS build
WORKDIR /app

COPY --from=common-build /root/.m2 /root/.m2

COPY notification-service/build.gradle notification-service/settings.gradle notification-service/gradlew ./
COPY notification-service/gradle gradle
RUN chmod +x gradlew

COPY notification-service/src src
RUN ./gradlew clean build -x test --no-daemon

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
