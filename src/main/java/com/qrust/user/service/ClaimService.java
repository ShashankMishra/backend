package com.qrust.user.service;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;

@ApplicationScoped
public class ClaimService {

    public boolean verifyClaim(UUID id, String code) {
        System.out.println("Verifying code claim for ID: " + code);
        return "1234556".equalsIgnoreCase(code);
    }
}
