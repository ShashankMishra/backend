package com.qrust.common.domain.order;

import com.fasterxml.jackson.databind.JsonNode;
import com.qrust.common.JsonUtil;
import com.qrust.user.api.dto.order.OrderItemType;
import lombok.SneakyThrows;
import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class OrderDetailsConverter implements AttributeConverter<OrderDetails> {
    @Override
    public AttributeValue transformFrom(OrderDetails orderDetails) {
        // Convert OrderDetails to a JSON string
        String json = toJson(orderDetails);
        return AttributeValue.builder().s(json).build();
    }

    @Override
    public OrderDetails transformTo(AttributeValue attributeValue) {
        // Convert the JSON string back to OrderDetails
        String json = attributeValue.s();
        return fromJson(json);
    }

    @Override
    public EnhancedType<OrderDetails> type() {
        return EnhancedType.of(OrderDetails.class);
    }

    @Override
    public AttributeValueType attributeValueType() {
        return AttributeValueType.S; // Stored as a string in DynamoDB
    }

    @SneakyThrows
    private static OrderDetails fromJson(String json) {
        var mapper = JsonUtil.createMapper();
        JsonNode node = mapper.readTree(json);
        OrderItemType type = OrderItemType.valueOf(node.get("orderItemType").asText());

        return switch (type) {
            case MEMBERSHIP -> mapper.treeToValue(node, MembershipOrderDetails.class);
            case QR_STICKER -> mapper.treeToValue(node, QrStickerOrderDetails.class);
        };
    }

    @SneakyThrows
    private static String toJson(OrderDetails orderDetails) {
        return JsonUtil.createMapper().writeValueAsString(orderDetails);
    }
}