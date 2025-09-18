package com.qrust.user.api.dto;

import com.qrust.common.domain.QRType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class LockscreenDetailsDto implements QRDetailsDto {
    @NotNull
    private QRType qrType = QRType.LOCKSCREEN;
    @NotBlank
    @Size(min = 1, max = 30)
    private String deviceName;
    @NotNull
    @Valid
    private ContactDto ownerContact;
    @NotNull
    @Valid
    private ContactDto emergencyContact;
    @Valid
    private MedicalDetailsDto medicalDetails;

    @Override
    public Set<ContactDto> getContactList() {
        Set<ContactDto> contacts = new HashSet<>();
        contacts.add(ownerContact);
        contacts.add(emergencyContact);
        return contacts;
    }
}

