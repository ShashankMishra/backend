package com.qrust.common.domain;

import lombok.Data;

@Data
public class LockscreenDetails implements QRDetails{
    private final QRType type = QRType.LOCKSCREEN;
    private String deviceName;
    private Contact ownerContact;
    private Contact emergencyContact;
    private MedicalDetails medicalDetails;
}
