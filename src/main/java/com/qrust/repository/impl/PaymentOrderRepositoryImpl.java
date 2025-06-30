package com.qrust.repository.impl;

import com.qrust.domain.OrderStatus;
import com.qrust.domain.PaymentOrder;
import com.qrust.repository.PaymentOrderRepository;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
@Slf4j
public class PaymentOrderRepositoryImpl implements PaymentOrderRepository {

    private final DynamoDbTable<PaymentOrder> paymentOrderTable;
    private final DynamoDbIndex<PaymentOrder> userIdIndex;

    public PaymentOrderRepositoryImpl(DynamoDbEnhancedClient enhancedClient) {
        this.paymentOrderTable = enhancedClient.table("paymentOrder", TableSchema.fromBean(PaymentOrder.class));
        this.userIdIndex = paymentOrderTable.index("userId-index");
    }

    @Override
    public List<PaymentOrder> getAllByUserId(String userId) {
        List<PaymentOrder> orders = new ArrayList<>();

        userIdIndex
                .query(r -> r.queryConditional(QueryConditional.keyEqualTo(k -> k.partitionValue(userId))))
                .stream()
                .forEach(page -> orders.addAll(page.items()));

        return orders;
    }

    @Override
    public PaymentOrder getByMerchantOrderId(String merchantOrderId) {
        Key key = Key.builder().partitionValue(merchantOrderId).build();
        return paymentOrderTable.getItem(key);
    }

    @Override
    public void save(PaymentOrder paymentOrder) {
        paymentOrderTable.putItem(paymentOrder);
    }

    @Override
    public List<PaymentOrder> getAllByOrderStatus(OrderStatus status) {
        List<PaymentOrder> orders = new ArrayList<>();
        paymentOrderTable.scan().items().forEach(order -> {
            if (order.getOrderStatus() == status) {
                orders.add(order);
            }
        });
        return orders;
    }
}
