#!/bin/sh
BRANCH=$(git branch | grep \* | cut -d ' ' -f2)
echo "Current branch is ${BRANCH}"
if [ "$BRANCH" = "master" ]
then
    echo "Building & publishing"
    ./gradlew :app:publishReleaseApk --daemon --parallel
else
    echo "Building only"
    ./gradlew :app:assembleRelease --daemon --parallel
fi
