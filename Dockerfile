FROM openjdk:8u171-jdk-alpine3.8 as builder

ADD . /app/server
WORKDIR /app/server

RUN chmod +x ./gradlew && \
    rm -rf app/ && \
    ./gradlew --no-daemon :server:shadowJar && \
    mv server/build/libs/server-1.0-all.jar /server.jar && \
    cd / && \
    rm -rf app && \
    rm -rf /root/.gradle

FROM openjdk:8u171-jre-alpine3.8 as environment
WORKDIR /app
COPY --from=builder /server.jar .
ENTRYPOINT java -jar /app/server.jar