package com.qrust.user.service;

import com.qrust.common.Utils;
import com.qrust.common.domain.Contact;
import com.qrust.common.domain.QRCode;
import com.qrust.common.domain.User;
import com.qrust.common.domain.order.PaymentOrder;
import com.qrust.common.domain.user.UserAddress;
import com.qrust.common.domain.user.UserInfo;
import com.qrust.common.repository.UserInfoRepository;
import com.qrust.user.api.dto.ContactOtp;
import com.qrust.user.api.dto.otp.OtpType;
import com.qrust.user.api.dto.otp.SendOtpRequest;
import com.qrust.user.api.dto.userinfo.UpgradeUserInfoRequest;
import com.qrust.user.api.dto.userinfo.UserInfoResponse;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;

@Slf4j
@RequestScoped
public class UserService {
    @Inject
    SecurityIdentity securityIdentity;

    @Inject
    OrderService orderService;

    @Inject
    MessageService messageService;

    @Inject
    WhatsappMessageService whatsappMessageService;

    @Inject
    UserInfoRepository userInfoRepository;

    public void addContact(String id, String number) {
        Contact contact = Contact.builder().name(id).phoneNumber(number).build();

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

    public String getOwnerName(QRCode qrCode) {
        //TODO: fix below logic after migration is done
        String userId = getCurrentUser().getUserId();
        if(userId == null)
            userId = qrCode.getOwner().getUserId();
        UserInfo userInfo = userInfoRepository.getByUserId(userId);
        Contact ownerContact = Utils.getOwnerContact(qrCode);
        return userInfo.getContacts().stream()
                .filter(x -> x.getPhoneNumber().equals(ownerContact.getPhoneNumber()))
                .findFirst().get().getName();
    }

    public String sendOtpForUser(SendOtpRequest sendOtpRequest) throws IOException {
        OtpType otpType = sendOtpRequest.getOtpType();
        String verificationId = "";

        if(otpType == OtpType.TEXT_OTP) {
           verificationId =  messageService.sendOtp(sendOtpRequest.getContactDto().getPhoneNumber());
        }else if(otpType == OtpType.WHATSAPP_OTP) {
           verificationId = whatsappMessageService.sendOtp(sendOtpRequest.getContactDto().getPhoneNumber());
        }

        return verificationId;
    }

    public boolean validateOtpForUser(ContactOtp contactOtp) throws IOException {
        OtpType otpType = contactOtp.getOtpType();

        if(otpType == OtpType.TEXT_OTP) {
           return messageService.validateOtp(contactOtp.getContactDto().getPhoneNumber(), contactOtp.getOtp(), contactOtp.getVerificationId());
        }else if(otpType == OtpType.WHATSAPP_OTP) {
           return whatsappMessageService.validateOtp(contactOtp.getVerificationId(), contactOtp.getOtp());
        }

        return false;
    }
}
