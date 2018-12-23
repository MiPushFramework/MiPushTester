#!/bin/sh
echo "Current branch is ${TRAVIS_BRANCH}"
if [ "$TRAVIS_BRANCH" = "master" ]
then
    echo "Building & publishing"
    ./gradlew :app:publishReleaseApk --daemon --parallel
else
    echo "Building only"
    ./gradlew :app:assembleRelease --daemon --parallel
fi
