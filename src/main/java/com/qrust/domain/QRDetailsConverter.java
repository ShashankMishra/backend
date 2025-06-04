package com.qrust.domain;

import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class QRDetailsConverter implements AttributeConverter<QRDetails> {
    @Override
    public AttributeValue transformFrom(QRDetails qrDetails) {
        // Convert QRDetails to a JSON string or another format suitable for DynamoDB
        String json = qrDetails.toJson(); // Assuming QRDetails has a toJson() method
        return AttributeValue.builder().s(json).build();
    }

    @Override
    public QRDetails transformTo(AttributeValue attributeValue) {
        // Convert the JSON string back to QRDetails
        String json = attributeValue.s();

        return QRDetails.fromJson(json); // Assuming QRDetails has a fromJson() method
    }

    @Override
    public EnhancedType<QRDetails> type() {
        return EnhancedType.of(QRDetails.class);
    }

    @Override
    public AttributeValueType attributeValueType() {
        return AttributeValueType.S; // Assuming the QRDetails are stored as a string in DynamoDB
    }
}
