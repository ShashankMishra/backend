package com.qrust.user.api.dto;

import lombok.Data;

@Data
public class ContactPreference {
    private ContactPolicyDto contactPolicy = ContactPolicyDto.ALWAYS;
    private String customMessage;

    public String getCustomMessage() {
        return contactPolicy.getDescription();
    }
}
