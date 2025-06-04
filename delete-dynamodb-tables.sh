#!/bin/bash
# Script to delete all relevant DynamoDB tables for the project

set -e

# List your table names here (update as needed)
TABLES=("QRCode")

for TABLE in "${TABLES[@]}"; do
  echo "Deleting table: $TABLE"
  aws dynamodb delete-table --table-name "$TABLE" --endpoint-url http://localhost:8000 || echo "Table $TABLE may not exist."
done

echo "All specified tables deleted (if they existed)."

