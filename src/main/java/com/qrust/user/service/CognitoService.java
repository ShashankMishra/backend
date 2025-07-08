package com.qrust.user.service;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminAddUserToGroupRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminRemoveUserFromGroupRequest;

@ApplicationScoped
public class CognitoService {
    @ConfigProperty(name = "cognito.userPoolId")
    String userPoolId;

    private final CognitoIdentityProviderClient cognitoClient = CognitoIdentityProviderClient.create();

    public void upgradeUserGroup(String username, String group) {

        // Add to premium group
        cognitoClient.adminAddUserToGroup(AdminAddUserToGroupRequest.builder()
                .userPoolId(userPoolId)
                .username(username)
                .groupName(group)
                .build());
        // Remove from normal group (optional, if you want exclusive group membership)
        /*cognitoClient.adminRemoveUserFromGroup(AdminRemoveUserFromGroupRequest.builder()
                .userPoolId(userPoolId)
                .username(username)
                .groupName("normal")
                .build());*/
    }

    public void downgradeUser(String username, String group) {
        cognitoClient.adminRemoveUserFromGroup(AdminRemoveUserFromGroupRequest.builder()
                .userPoolId(userPoolId)
                .username(username)
                .groupName(group)
                .build());
    }
}

