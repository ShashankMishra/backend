package com.qrust.user.api.dto.order;

import com.qrust.common.domain.user.UserAddress;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class QRStickerOrderItem extends OrderItem {
    private StickerType stickerType;
    private int templateId;
    private int quantity;
    private UserAddress userAddress;

    public QRStickerOrderItem() {
        setOrderItemType(OrderItemType.QR_STICKER);
    }

    @Override
    public int calculatePrice() {
        switch (stickerType) {
            case VEHICLE:
                return 189 * quantity;
            default:
                return 249 * quantity;
        }
    }
}