package com.qrust.domain;

import lombok.Data;

import java.util.Optional;

@Data
public class LockscreenDetails implements QRDetails{
    private final QRType type = QRType.LOCKSCREEN;
    private String deviceName;
    private Contact ownerContact;
    private Contact emergencyContact;
    private Optional<MedicalDetails> medicalDetails;
}
