package com.qrust.user.api.dto.order;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class QRStickerOrderItem extends OrderItem {
    private StickerType stickerType;
    private int templateId;
    private int quantity;
    
    public QRStickerOrderItem() {
        setOrderItemType(OrderItemType.QR_STICKER);
    }
    
    @Override
    public int calculatePrice() {
        // All sticker types currently have the same price
        return 499 * quantity;
    }
}