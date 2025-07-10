package com.qrust.user.api.dto.order;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

@Data
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "orderItemType"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = MembershipOrderItem.class, name = "MEMBERSHIP"),
    @JsonSubTypes.Type(value = QRStickerOrderItem.class, name = "QR_STICKER")
})
public abstract class OrderItem {
    private OrderItemType orderItemType;

    public abstract int calculatePrice();
}