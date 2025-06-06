#!/bin/bash

# Script to create DynamoDB tables in local DynamoDB (LocalStack)
ENDPOINT_URL="http://localhost:8000"
REGION="us-east-1"


# Create QRCode table
aws dynamodb create-table \
  --table-name QRCodeTable \
  --attribute-definitions AttributeName=id,AttributeType=S \
  --key-schema AttributeName=id,KeyType=HASH \
  --provisioned-throughput ReadCapacityUnits=5,WriteCapacityUnits=5 \
  --endpoint-url $ENDPOINT_URL \
  --region $REGION || true

echo "Tables created in local DynamoDB."

