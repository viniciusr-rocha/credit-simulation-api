#!/bin/bash

echo "Running Gradle clean and build..."
./gradlew clean build

if [ $? -eq 0 ]; then
    echo "Gradle build succeeded. Starting Docker Compose..."
    docker compose up -d

    echo "Displaying logs of the running containers..."
    docker logs credit-simulation-api -f
else
    echo "Gradle build failed. Aborting Docker Compose startup."
    exit 1
fi
