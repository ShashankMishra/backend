package com.qrust.common.repository.impl;

import com.qrust.common.domain.order.OrderStatus;
import com.qrust.common.domain.order.PaymentOrder;
import com.qrust.common.domain.order.PaymentStatus;
import com.qrust.common.repository.OrderRepository;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
@Slf4j
public class OrderRepositoryImpl implements OrderRepository {

    private final DynamoDbTable<PaymentOrder> paymentOrderTable;
    private final DynamoDbIndex<PaymentOrder> userIdIndex;
    private final DynamoDbIndex<PaymentOrder> paymentStatusIndex;
    private final DynamoDbIndex<PaymentOrder> orderStatusIndex;

    public OrderRepositoryImpl(DynamoDbEnhancedClient enhancedClient) {
        this.paymentOrderTable = enhancedClient.table("paymentOrder", TableSchema.fromBean(PaymentOrder.class));
        this.userIdIndex = paymentOrderTable.index("GSI_UserOrders");
        this.paymentStatusIndex = paymentOrderTable.index("GSI_PaymentStatus");
        this.orderStatusIndex = paymentOrderTable.index("GSI_OrderStatus");
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
    public List<PaymentOrder> getAllByMerchantOrderId(String merchantOrderId) {
        List<PaymentOrder> orders = new ArrayList<>();

        paymentOrderTable
                .query(r -> r.queryConditional(QueryConditional.keyEqualTo(k -> k.partitionValue(merchantOrderId))))
                .stream()
                .forEach(page -> orders.addAll(page.items()));

        return orders;
    }

    @Override
    public void save(PaymentOrder paymentOrder) {
        paymentOrderTable.putItem(paymentOrder);
    }

    @Override
    public List<PaymentOrder> getAllByPaymentStatus(PaymentStatus status) {
        List<PaymentOrder> orders = new ArrayList<>();

        paymentStatusIndex
                .query(r -> r.queryConditional(QueryConditional.keyEqualTo(k -> k.partitionValue(status.name()))))
                .stream()
                .forEach(page -> orders.addAll(page.items()));

        return orders;
    }

    @Override
    public List<PaymentOrder> getAllQrStickerOrders(OrderStatus orderStatus) {
        List<PaymentOrder> orders = new ArrayList<>();

        orderStatusIndex
                .query(r -> r.queryConditional(QueryConditional.keyEqualTo(k -> k.partitionValue(orderStatus.name()))))
                .stream()
                .forEach(page -> orders.addAll(page.items()));

        return orders;
    }

    @Override
    public PaymentOrder getByOrderItemId(String orderItemId) {
        return paymentOrderTable.scan()
                .items()
                .stream()
                .filter(order -> orderItemId.equals(order.getOrderItemId()))
                .findFirst()
                .get();
    }
}
