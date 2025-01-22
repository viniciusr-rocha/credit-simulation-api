#!/bin/bash

echo "Running Gradle clean and build..."
./gradlew clean build

if [ $? -eq 0 ]; then
    echo "Gradle build succeeded. Starting Docker Compose..."
    docker compose up -d
else
    echo "Gradle build failed. Aborting Docker Compose startup."
    exit 1
fi
