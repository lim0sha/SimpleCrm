# Многоэтапная сборка для безопасности
FROM openjdk:21-jdk-slim AS builder

RUN apt-get update && apt-get install -y --no-install-recommends \
    curl \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY gradle.properties .

RUN chmod +x ./gradlew

COPY Application Application
COPY Types Types
COPY Dto Dto
COPY DataAccess DataAccess
COPY Presentation Presentation

RUN ./gradlew clean build -x test --no-daemon

FROM openjdk:21-jre-slim

RUN apt-get update && apt-get install -y --no-install-recommends \
    postgresql-client \
    && rm -rf /var/lib/apt/lists/* \
    && rm -rf /var/cache/apt/*

RUN groupadd --system javauser && \
    useradd --system --gid javauser javauser

WORKDIR /app

COPY --from=builder --chown=javauser:javauser /app/Presentation/build/libs/*.jar app.jar

USER javauser

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
