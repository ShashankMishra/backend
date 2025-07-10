package com.qrust.user.api.dto.order;

import com.qrust.user.api.dto.PlanType;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class MembershipOrderItem extends OrderItem {
    private PlanType planType;
    
    public MembershipOrderItem() {
        setOrderItemType(OrderItemType.MEMBERSHIP);
    }
    
    @Override
    public int calculatePrice() {
        switch (planType) {
            case BASIC:
                return 249;
            case PREMIUM:
                return 100000;
            case BUSINESS:
                return 100000;
            default:
                throw new IllegalArgumentException("Unsupported plan type: " + planType);
        }
    }
}