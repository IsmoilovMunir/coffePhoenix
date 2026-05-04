# Сборка и запуск API заявок (Spring Boot 3, Java 17)
# Образы с public.ecr.aws — те же library/* что на Docker Hub, но без анонимного rate limit Hub.
FROM public.ecr.aws/docker/library/maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /src
COPY pom.xml .
COPY src ./src
RUN mvn -q -e -DskipTests package \
  && JAR="$(ls target/*.jar | grep -v '\.jar\.original$' | head -1)" \
  && test -n "$JAR" && cp "$JAR" /src/app.jar

FROM public.ecr.aws/docker/library/eclipse-temurin:17-jre-jammy
WORKDIR /app
RUN groupadd -r spring && useradd -r -g spring spring
COPY --from=build /src/app.jar /app/app.jar
RUN chown spring:spring /app/app.jar
USER spring:spring
EXPOSE 8080
ENV JAVA_OPTS=""
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
