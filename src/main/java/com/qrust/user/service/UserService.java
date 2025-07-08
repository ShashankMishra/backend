package com.qrust.user.service;

import com.qrust.common.domain.PaymentOrder;
import com.qrust.common.domain.User;
import com.qrust.common.domain.UserRole;
import com.qrust.common.repository.OrderRepository;
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
}
