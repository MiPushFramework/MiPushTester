#!/bin/sh
echo "Current branch is ${TRAVIS_BRANCH}"
if [ "$TRAVIS_BRANCH" = "master" ] && [ "$TRAVIS_PULL_REQUEST" = "false" ]
then
    echo "Building & publishing"
    ./gradlew :app:publishReleaseApk --daemon --parallel
else
    echo "Building only"
    ./gradlew :app:assembleRelease --daemon --parallel
fi
