#!/bin/bash

# Script to create DynamoDB tables in local DynamoDB (LocalStack)
ENDPOINT_URL="http://localhost:8000"
REGION="us-east-1"

# Create UserProfile table
aws dynamodb create-table \
  --table-name UserTable \
  --attribute-definitions AttributeName=profileId,AttributeType=S \
  --key-schema AttributeName=profileId,KeyType=HASH \
  --provisioned-throughput ReadCapacityUnits=5,WriteCapacityUnits=5 \
  --endpoint-url $ENDPOINT_URL \
  --region $REGION || true

# Create QRCode table
aws dynamodb create-table \
  --table-name QRCodeTable \
  --attribute-definitions AttributeName=id,AttributeType=S \
  --key-schema AttributeName=id,KeyType=HASH \
  --provisioned-throughput ReadCapacityUnits=5,WriteCapacityUnits=5 \
  --endpoint-url $ENDPOINT_URL \
  --region $REGION || true

# Create UserQRCodeLink table
aws dynamodb create-table \
  --table-name UserQRCodeLinkTable \
  --attribute-definitions AttributeName=linkId,AttributeType=S \
  --key-schema AttributeName=linkId,KeyType=HASH \
  --provisioned-throughput ReadCapacityUnits=5,WriteCapacityUnits=5 \
  --endpoint-url $ENDPOINT_URL \
  --region $REGION || true

echo "Tables created in local DynamoDB."

