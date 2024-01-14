# Build Stage
FROM gradle:jdk17 as builder

COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src

RUN gradle clean build --no-daemon

# Runtime Stage
FROM openjdk:17-jdk-slim

# Copy the specific JAR file
COPY --from=builder /home/gradle/src/build/libs/admin-service.jar /app/

ENTRYPOINT ["java", "-jar", "/app/admin-service.jar"]
