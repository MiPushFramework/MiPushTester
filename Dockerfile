FROM openjdk:8u171-jdk-alpine3.8 as builder

ADD . /app/server
WORKDIR /app/server

# Git is used for reading version
RUN apk add git && \
    chmod +x ./gradlew && \
    rm -rf app/ && \
    ./gradlew :common:build && \
    ./gradlew :server:jar && \
    mv server/build/libs/server-1.0.jar /server.jar

FROM openjdk:8u171-jre-alpine3.8 as environment
WORKDIR /app
COPY --from=builder /server.jar .
CMD java -jar /app/server.jar
