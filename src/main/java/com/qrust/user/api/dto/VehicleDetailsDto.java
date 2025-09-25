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
public class VehicleDetailsDto implements QRDetailsDto {
    @NotNull
    private QRType qrType = QRType.VEHICLE;
    @NotBlank
    @Size(min = 4, max = 15)
    private String numberPlate;
    @NotBlank
    @Size(min = 1, max = 20)
    private String modelDescription;
    @Valid
    private MedicalDetailsDto medicalDetails;
    @Valid
    @NotNull
    private ContactDto ownerContact;
    @NotNull
    @Valid
    private ContactDto emergencyContact;

    @Override
    public Set<ContactDto> getContactList() {
        Set<ContactDto> contacts = new HashSet<>();
        contacts.add(ownerContact);
        contacts.add(emergencyContact);
        return contacts;
    }
}

