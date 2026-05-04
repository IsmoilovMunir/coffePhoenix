# Сборка и запуск API заявок (Spring Boot 3, Java 17)
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /src
COPY pom.xml .
COPY src ./src
RUN mvn -q -e -DskipTests package \
  && cp target/franchise-api-0.0.1-SNAPSHOT.jar /src/app.jar

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
RUN groupadd -r spring && useradd -r -g spring spring
COPY --from=build /src/app.jar /app/app.jar
RUN chown spring:spring /app/app.jar
USER spring:spring
EXPOSE 8080
ENV JAVA_OPTS=""
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
