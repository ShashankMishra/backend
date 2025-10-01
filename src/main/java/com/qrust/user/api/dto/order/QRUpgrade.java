package com.qrust.user.api.dto.order;

import com.qrust.common.domain.user.UserAddress;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
public class QRUpgrade extends OrderItem {
    private UUID qrCodeId;
    private UserAddress userAddress;

    public QRUpgrade() {
        setOrderItemType(OrderItemType.QR_UPGRADE);
    }

    @Override
    public int calculatePrice() {
        return 149;
    }
}