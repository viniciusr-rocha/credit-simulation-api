#!/bin/bash

echo "Stopping and removing existing containers..."
docker compose down

if [ $? -eq 0 ]; then
    echo "Application successfully stopped and containers removed."
else
    echo "Failed to stop and remove containers."
    exit 1
fi
