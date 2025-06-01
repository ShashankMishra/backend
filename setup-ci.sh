#!/bin/bash
# setup-ci.sh: Script to start Docker Compose and create DynamoDB tables for CI
set -e

echo "[CI Setup] Starting Docker Compose..."
docker compose up -d

echo "[CI Setup] Waiting for DynamoDB Local to be ready..."
# Wait for DynamoDB Local to be available (adjust port if needed)
for i in {1..10}; do
  if nc -z localhost 8000; then
    echo "[CI Setup] DynamoDB Local is up!"
    break
  fi
  echo "[CI Setup] Waiting for DynamoDB Local (attempt $i)..."
  sleep 3
done

echo "[CI Setup] Creating DynamoDB tables..."
bash create-dynamodb-tables.sh

echo "[CI Setup] Setup complete."

