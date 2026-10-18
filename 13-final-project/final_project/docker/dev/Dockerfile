# syntax=docker/dockerfile:1.6

FROM eclipse-temurin:25-jdk-alpine AS build
USER root

RUN apk upgrade --no-cache

WORKDIR /workspace

COPY --chmod=0755 mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY base-api/pom.xml base-api/pom.xml
COPY base-consumer/pom.xml base-consumer/pom.xml

ENV MAVEN_OPTS="-XX:-TieredCompilation -XX:TieredStopAtLevel=1"

RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw -B \
    -Daether.dependencyCollector.impl=bf \
    dependency:go-offline

COPY base-api/src base-api/src
COPY base-consumer/src base-consumer/src

RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw -T 1C -B clean package -DskipTests -pl base-consumer -am && \
    JAR_FILE="$(ls base-consumer/target/*.jar | grep -v 'original' | head -n 1)" && \
    cp "${JAR_FILE}" app.jar

FROM eclipse-temurin:25-jre-alpine AS runtime
WORKDIR /app

RUN apk upgrade --no-cache && \
    addgroup -g 185 -S appgroup && \
    adduser -u 185 -S appuser -G appgroup && \
    mkdir -p /app/logs && chown -R 185:185 /app/logs

USER 185

COPY --from=build /workspace/app.jar ./app.jar

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=5s --start-period=30s --retries=3 \
    CMD wget -qO- http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "/app/app.jar"]