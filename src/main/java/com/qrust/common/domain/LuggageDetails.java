package com.qrust.common.domain;

import lombok.Data;

@Data
public class LuggageDetails implements QRDetails {
    private final QRType type = QRType.LUGGAGE;
    private String description;
    private Contact ownerContact;
    private Contact emergencyContact;
}

