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
public class ChildDetailsDto implements QRDetailsDto {
    @NotNull
    private QRType qrType = QRType.CHILD;
    @NotBlank
    @Size(max = 20)
    private String fullName;
    @Size(max = 30)
    private String schoolName;
    @Valid
    @NotNull
    private ContactDto emergencyContact;
    @Valid
    private MedicalDetailsDto medicalDetails;

    @Override
    public Set<ContactDto> getContactList() {
        Set<ContactDto> contacts = new HashSet<>();
        contacts.add(emergencyContact);
        return contacts;
    }
}

