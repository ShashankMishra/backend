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

# Create scanHistory table with GSI for scannerIp and qrId
aws dynamodb create-table \
  --table-name scanHistory \
  --attribute-definitions AttributeName=scanId,AttributeType=S AttributeName=scannerIp,AttributeType=S AttributeName=qrId,AttributeType=S AttributeName=scanTimestamp,AttributeType=S \
  --key-schema AttributeName=scanId,KeyType=HASH \
  --global-secondary-indexes '[
    {
      "IndexName": "scannerIp-qrId-index",
      "KeySchema": [
        {"AttributeName":"scannerIp","KeyType":"HASH"},
        {"AttributeName":"qrId","KeyType":"RANGE"}
      ],
      "Projection":{"ProjectionType":"ALL"},
      "ProvisionedThroughput":{"ReadCapacityUnits":5,"WriteCapacityUnits":5}
    },
    {
      "IndexName": "qrId-index",
      "KeySchema": [
        {"AttributeName":"qrId","KeyType":"HASH"},
        {"AttributeName":"scanTimestamp","KeyType":"RANGE"}

      ],
      "Projection":{"ProjectionType":"ALL"},
      "ProvisionedThroughput":{"ReadCapacityUnits":5,"WriteCapacityUnits":5}
    }
  ]' \
  --provisioned-throughput ReadCapacityUnits=5,WriteCapacityUnits=5 \
  --endpoint-url $ENDPOINT_URL \
  --region $REGION || true

# Create paymentOrder table with merchantOrderId as HASH key, orderItemId as RANGE key, and GSIs
aws dynamodb create-table \
  --table-name paymentOrder \
  --attribute-definitions AttributeName=merchantOrderId,AttributeType=S AttributeName=orderItemId,AttributeType=S AttributeName=userId,AttributeType=S AttributeName=paymentStatus,AttributeType=S AttributeName=orderStatus,AttributeType=S AttributeName=createdAt,AttributeType=S \
  --key-schema AttributeName=merchantOrderId,KeyType=HASH AttributeName=orderItemId,KeyType=RANGE \
  --global-secondary-indexes '[
    {
      "IndexName": "GSI_UserOrders",
      "KeySchema": [
        {"AttributeName":"userId","KeyType":"HASH"},
        {"AttributeName":"createdAt","KeyType":"RANGE"}
      ],
      "Projection":{"ProjectionType":"ALL"},
      "ProvisionedThroughput":{"ReadCapacityUnits":5,"WriteCapacityUnits":5}
    },
    {
      "IndexName": "GSI_PaymentStatus",
      "KeySchema": [
        {"AttributeName":"paymentStatus","KeyType":"HASH"},
        {"AttributeName":"createdAt","KeyType":"RANGE"}
      ],
      "Projection":{"ProjectionType":"ALL"},
      "ProvisionedThroughput":{"ReadCapacityUnits":5,"WriteCapacityUnits":5}
    },
    {
      "IndexName": "GSI_OrderStatus",
      "KeySchema": [
        {"AttributeName":"orderStatus","KeyType":"HASH"},
        {"AttributeName":"createdAt","KeyType":"RANGE"}
      ],
      "Projection":{"ProjectionType":"ALL"},
      "ProvisionedThroughput":{"ReadCapacityUnits":5,"WriteCapacityUnits":5}
    }
  ]' \
  --provisioned-throughput ReadCapacityUnits=5,WriteCapacityUnits=5 \
  --endpoint-url $ENDPOINT_URL \
  --region $REGION || true

aws dynamodb create-table \
    --table-name UserInfo \
    --attribute-definitions AttributeName=userId,AttributeType=S \
    --key-schema AttributeName=userId,KeyType=HASH \
    --provisioned-throughput ReadCapacityUnits=5,WriteCapacityUnits=5 \
    --endpoint-url $ENDPOINT_URL \
    --region $REGION || true

echo "Tables created in local DynamoDB."
