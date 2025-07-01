package com.qrust.service;

import com.qrust.domain.User;
import com.qrust.domain.PaymentOrder;

import java.util.List;

public interface UserService {
    User getCurrentUser();
    User getUserById(String userId);
    boolean isUserInGroup(String userId, String group);
    List<PaymentOrder> getOrdersForCurrentUser();
}
