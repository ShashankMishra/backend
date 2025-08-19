package com.qrust.user.service;

import com.qrust.common.domain.Contact;
import com.qrust.common.domain.User;
import com.qrust.common.domain.order.PaymentOrder;
import com.qrust.common.domain.user.UserAddress;
import com.qrust.common.domain.user.UserInfo;
import com.qrust.common.repository.UserInfoRepository;
import com.qrust.user.api.dto.userinfo.UpgradeUserInfoRequest;
import com.qrust.user.api.dto.userinfo.UserInfoResponse;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@RequestScoped
public class UserService {
    @Inject
    SecurityIdentity securityIdentity;

    @Inject
    OrderService orderService;

    @Inject
    UserInfoRepository userInfoRepository;

    public void addContact(String id, String number) {
        Contact contact = new Contact();
        contact.setName(id);
        contact.setPhoneNumber(number);

        String userId = getCurrentUser().getUserId();
        UserInfo userInfo = userInfoRepository.getByUserId(userId);
        userInfo.addContact(contact);
        userInfoRepository.save(userInfo);
        log.info("Contact added for user '{}': {}", userId, contact);
    }

    public User getCurrentUser() {
        String sub = securityIdentity.getPrincipal().getName();
        return new User(sub, null);
    }

    public List<PaymentOrder> getOrdersForCurrentUser() {
        String userId = getCurrentUser().getUserId();
        return orderService.getAllByUserId(userId);
    }

    public UserInfoResponse getUserInfoResponse() {
        String userId = getCurrentUser().getUserId();
        UserInfo userInfo = userInfoRepository.getByUserId(userId);
        return new UserInfoResponse(userInfo.getAddresses());
    }

    public void upgradeUserInfo(UpgradeUserInfoRequest request) {
        String userId = getCurrentUser().getUserId();
        String addressId = request.getUserAddress().getAddressId();
        UserInfo userInfo = userInfoRepository.getByUserId(userId);

        UserAddress userAddress = userInfo.getAddresses().stream()
                .filter(a -> a.getAddressId().equals(addressId))
                .findFirst()
                .orElseGet(() -> {
                    UserAddress newAddress = new UserAddress();
                    newAddress.setAddressId(java.util.UUID.randomUUID().toString());
                    userInfo.getAddresses().add(newAddress);
                    return newAddress;
                });

        userAddress.setAddressLine1(request.getUserAddress().getAddressLine1());
        userAddress.setAddressLine2(request.getUserAddress().getAddressLine2());
        userAddress.setCity(request.getUserAddress().getCity());
        userAddress.setPincode(request.getUserAddress().getPincode());
        userAddress.setPhoneNumber(request.getUserAddress().getPhoneNumber());
        userAddress.setName(request.getUserAddress().getName());

        userInfoRepository.save(userInfo);
    }

    public void removeUserAddress(String addressId) {
        String userId = getCurrentUser().getUserId();
        UserInfo userInfo = userInfoRepository.getByUserId(userId);
        if (addressId != null && !addressId.isEmpty()) {
            userInfo.getAddresses().removeIf(address -> address.getAddressId().equals(addressId));
            userInfoRepository.save(userInfo);
        } else {
            log.warn("Address ID is null or empty, cannot remove address for user: {}", userId);
        }
    }

    public UserInfo getCurrentUserInfo() {
        String userId = getCurrentUser().getUserId();
        return userInfoRepository.getByUserId(userId);
    }
}
