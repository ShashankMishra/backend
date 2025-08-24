package com.qrust.common.domain.order;

import com.qrust.user.api.dto.order.OrderItemType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

import java.util.UUID;

@Data
@DynamoDbBean
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class QRUpgradeOrderDetails extends OrderDetails {
    private UUID qrCodeId;

    public QRUpgradeOrderDetails(UUID qrCodeId) {
        this.qrCodeId = qrCodeId;
        setOrderItemType(OrderItemType.QR_UPGRADE);
    }
}
