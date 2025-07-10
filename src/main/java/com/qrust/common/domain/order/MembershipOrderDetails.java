package com.qrust.common.domain.order;

import com.qrust.user.api.dto.PlanType;
import com.qrust.user.api.dto.order.OrderItemType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

@Data
@DynamoDbBean
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class MembershipOrderDetails extends OrderDetails {
    private PlanType planType;

    public MembershipOrderDetails(PlanType planType) {
        this.planType = planType;
        setOrderItemType(OrderItemType.MEMBERSHIP);
    }
}
