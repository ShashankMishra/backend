package com.qrust.service.impl;

import com.qrust.domain.User;
import com.qrust.domain.UserRole;
import com.qrust.service.UserService;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminListGroupsForUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminListGroupsForUserResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.CognitoIdentityProviderException;

import com.qrust.domain.PaymentOrder;
import com.qrust.repository.PaymentOrderRepository;
import java.util.List;

import static com.qrust.domain.UserRole.FREE;
import static software.amazon.awssdk.regions.Region.AP_SOUTH_1;

@Slf4j
@RequestScoped
@Named
public class UserServiceImpl implements UserService {
    @Inject
    SecurityIdentity securityIdentity;

    @ConfigProperty(name = "cognito.userPoolId")
    String userPoolId;

    private final Region region = AP_SOUTH_1;

    @Inject
    PaymentOrderRepository paymentOrderRepository;

    @Override
    public User getCurrentUser() {
        String sub = securityIdentity.getPrincipal().getName();
        return  new User(sub, FREE);
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
            UserRole role = UserRole.FREE;
            if (!response.groups().isEmpty()) {
                String groupName = response.groups().get(0).groupName();
                try {
                    role = UserRole.valueOf(groupName.toUpperCase());
                } catch (IllegalArgumentException e) {
                    log.warn("Unknown group name '{}' for user '{}', defaulting to FREE", groupName, userId);
                }
            }
            log.info("Retrieved user '{}' with role '{}'", userId, role);
            return new User(userId, role);
        } catch (CognitoIdentityProviderException e) {
            log.error("Error retrieving user '{}' from Cognito: {}", userId, e.awsErrorDetails().errorMessage());
            return new User(userId, UserRole.FREE);
        }
    }

    @Override
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

    @Override
    public List<PaymentOrder> getOrdersForCurrentUser() {
        String userId = getCurrentUser().getUserId();
        return paymentOrderRepository.getAllByUserId(userId);
    }
}
