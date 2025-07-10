package com.qrust.common.domain.order;

import com.qrust.user.api.dto.order.OrderItemType;
import com.qrust.user.api.dto.order.StickerType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

@Data
@DynamoDbBean
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class QrStickerOrderDetails extends OrderDetails {
    private StickerType stickerType;
    private int quantity;
    private int templateId;
    private String shippingId;
    private String serialNumber;
    
    public QrStickerOrderDetails(StickerType stickerType, int quantity, int templateId) {
        this.stickerType = stickerType;
        this.quantity = quantity;
        this.templateId = templateId;
        setOrderItemType(OrderItemType.QR_STICKER);
    }
}