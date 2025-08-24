package com.qrust.user.service;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;

@ApplicationScoped
public class CognitoService {
    @ConfigProperty(name = "cognito.userPoolId")
    String userPoolId;

    private final CognitoIdentityProviderClient cognitoClient = CognitoIdentityProviderClient.create();

    // get user details by username



}

