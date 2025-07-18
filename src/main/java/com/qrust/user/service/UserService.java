package com.qrust.user.service;

import com.qrust.common.domain.order.PaymentOrder;
import com.qrust.common.domain.User;
import com.qrust.common.domain.UserRole;
import com.qrust.common.domain.user.UserAddress;
import com.qrust.common.domain.user.UserInfo;
import com.qrust.common.repository.OrderRepository;
import com.qrust.common.repository.UserInfoRepository;
import com.qrust.user.api.dto.userinfo.UpgradeUserInfoRequest;
import com.qrust.user.api.dto.userinfo.UserInfoResponse;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminListGroupsForUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminListGroupsForUserResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.CognitoIdentityProviderException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.qrust.common.domain.UserRole.FREE;
import static software.amazon.awssdk.regions.Region.AP_SOUTH_1;

@Slf4j
@RequestScoped
public class UserService {
    @Inject
    SecurityIdentity securityIdentity;

    @ConfigProperty(name = "cognito.userPoolId")
    String userPoolId;

    private final Region region = AP_SOUTH_1;

    @Inject
    OrderRepository orderRepository;

    @Inject
    UserInfoRepository userInfoRepository;

    public User getCurrentUser() {
        String sub = securityIdentity.getPrincipal().getName();
        return new User(sub, new HashSet<>(List.of(FREE)));
    }

    public User getUserById(String userId) {
        try (CognitoIdentityProviderClient cognitoClient = CognitoIdentityProviderClient.builder()
                .region(region)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build()) {

            AdminListGroupsForUserRequest request = AdminListGroupsForUserRequest.builder()
                    .username(userId)
                    .userPoolId(userPoolId)
                    .build();
            AdminListGroupsForUserResponse response = cognitoClient.adminListGroupsForUser(request);
            Set<UserRole> role = new HashSet<>();
            role.add(FREE);
            if (!response.groups().isEmpty()) {
                try {
                    for (var group : response.groups()) {
                        String groupName = group.groupName();
                        log.debug("User '{}' is in group '{}'", userId, groupName);
                        // Convert group name to UserRole
                        role.add(UserRole.valueOf(groupName.toUpperCase()));
                    }
                } catch (IllegalArgumentException e) {
                    log.warn("Unknown group name '{}' for user '{}', defaulting to FREE", response.groups(), userId);
                }
            }
            log.info("Retrieved user '{}' with role '{}'", userId, role);
            return new User(userId, role);
        } catch (CognitoIdentityProviderException e) {
            log.error("Error retrieving user '{}' from Cognito: {}", userId, e.awsErrorDetails().errorMessage());
            return new User(userId, new HashSet<>(List.of(FREE)));
        }
    }

    public boolean isUserInGroup(String userId, String group) {
        try (CognitoIdentityProviderClient cognitoClient = CognitoIdentityProviderClient.builder()
                .region(region)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build()) {

            AdminListGroupsForUserRequest request = AdminListGroupsForUserRequest.builder()
                    .username(userId)
                    .userPoolId(userPoolId)
                    .build();
            AdminListGroupsForUserResponse response = cognitoClient.adminListGroupsForUser(request);
            return response.groups().stream()
                    .anyMatch(g -> g.groupName().equalsIgnoreCase(group));
        } catch (CognitoIdentityProviderException e) {
            log.error("Error checking user '{}' group '{}' in Cognito: {}", userId, group, e.awsErrorDetails().errorMessage());
            return false;
        }
    }

    public List<PaymentOrder> getOrdersForCurrentUser() {
        String userId = getCurrentUser().getUserId();
        return orderRepository.getAllByUserId(userId);
    }

    public UserInfoResponse getUserInfoResponse(){
        String userId = getCurrentUser().getUserId();
        UserInfo userInfo = userInfoRepository.getByUserId(userId);
        return new UserInfoResponse(userInfo.getAddresses());
    }

    public void upgradeUserInfo(UpgradeUserInfoRequest request) {
        String userId = getCurrentUser().getUserId();
        String addressId = request.getUserAddress().getAddressId();
        UserInfo userInfo = userInfoRepository.getByUserId(userId);

        if(addressId == null || addressId.isEmpty()) {
            UserAddress newAddress = request.getUserAddress();
            newAddress.setAddressId(java.util.UUID.randomUUID().toString());
            newAddress.setAddressLine1(request.getUserAddress().getAddressLine1());
            newAddress.setAddressLine2(request.getUserAddress().getAddressLine2());
            newAddress.setCity(request.getUserAddress().getCity());
            newAddress.setPincode(request.getUserAddress().getPincode());
            newAddress.setPhoneNumber(request.getUserAddress().getPhoneNumber());

            userInfo.getAddresses().add(newAddress);
        }else{
            UserAddress existingUserAddress = userInfo.getAddresses().stream().filter(a -> a.getAddressId().equals(addressId)).findFirst().get();
            existingUserAddress.setAddressLine1(request.getUserAddress().getAddressLine1());
            existingUserAddress.setAddressLine2(request.getUserAddress().getAddressLine2());
            existingUserAddress.setCity(request.getUserAddress().getCity());
            existingUserAddress.setPincode(request.getUserAddress().getPincode());
            existingUserAddress.setPhoneNumber(request.getUserAddress().getPhoneNumber());
        }

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
}
