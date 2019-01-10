FROM openjdk:8u171-jdk-alpine3.8 as builder

ADD . /app/server
WORKDIR /app/server

# Git is used for reading version
# ShadowJar does not support Gradle 5+, so use 4.10.1 to build the JAR
# Known issues: it will still download Gradle 5.1 before downloading 4.10.1
RUN apk add git && \
    chmod +x ./gradlew && \
    rm -rf app/ && \
    ./gradlew exportVersion && \
    ./gradlew :server:shadowJar && \
    mv server/build/libs/server-$(cat version.txt).jar /server.jar

FROM openjdk:8u171-jre-alpine3.8 as environment
WORKDIR /app
COPY --from=builder /server.jar .
CMD java -jar /app/server.jar
