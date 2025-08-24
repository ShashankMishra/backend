package com.qrust.common.domain.order;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.qrust.user.api.dto.order.OrderItemType;
import lombok.Data;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

@Data
@DynamoDbBean
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "orderItemType"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = QRUpgradeOrderDetails.class, name = "QR_UPGRADE"),
    @JsonSubTypes.Type(value = QrStickerOrderDetails.class, name = "QR_STICKER")
})
public abstract class OrderDetails {
    @JsonIgnore
    private OrderItemType orderItemType;
}
