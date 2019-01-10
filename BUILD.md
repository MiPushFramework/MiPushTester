# Building guides
This guide tells you how to build this project and run it on your own devices.
It has two parts - Server (Docker) and client (Android).

# Limits
The relationship between the client and server is one to one, that means the "Official" APKs can only use "Official" server, your builds can't use it. So if you'd like to build the client, you will have to build the server as well.

Update checker will also not works on your builds, it only supports official clients.

# Pre-requirements
Whatever to build the server or client, you should have a MiPush application which is registered in Mi dev console at first.

You need to register a Mi account and submit your real-name information then create a application with a custom package name.

# Server

## Requirements
* Docker

The server part is written in Java and deployed with Docker. Please make sure Docker is installed.

## Build the server
Building the server is pretty easy, just execute `docker build`:
```shell
$ cd server
$ docker built -t mipush .
```

## Run the server
Firstly, write your `app secret` to environment variables:
```shell
$ echo "MIPUSH_AUTH=<Your App Secret>" > .env
```
`.env` is the file which stores private keys and pass them into docker container.
For more details about this file, take a look at `server/.env.template`.

Finally, start the container:
```shell
$ docker run \
    -p 8080:8080 \
    --env-file ./.env \
    thnuiwelr/mipush # From docker hub, or you can use your local image
```
It starts, you can visit `<host>:8080` now.

### Run via docker compose

#### Requirements
* Docker
* Docker-compose

Running via docker compose is better than using the strange docker command every time. You can copy this `docker-compose.yml`:
```yml
version: "3"
services:
    web:
        image: mipush
        ports:
            - 8080:8080
        env_file:
            - ./.env
```
Then, execute this command to start it:
```shell
$ docker-compose up
```

# Build the client

## Requirements
* Android SDK
* Android SDK Build Tools 28.0.3
* Android SDK Platform 28
* (Maybe) Android Studio

You can't use the same package name as the "Official" builds, you should change it to adapt your own `app id` which is registered in dev console.

Just copy `app/xmpush.properties.template` to `app/xmpush.properties`, and change the values.

Finally, run

```shell
$ ./gradlew :app:assembleRelease
```

to generate the APK.