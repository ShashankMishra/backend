package com.qrust.common.domain;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class VehicleDetails implements QRDetails {
    private final QRType type = QRType.VEHICLE;
    private String numberPlate;
    private String modelDescription;
    private Contact ownerContact;
    private Contact emergencyContact;
    private MedicalDetails medicalDetails;

    @Override
    public Set<Contact> getContacts() {
        Set<Contact> contacts = new HashSet<>();
        contacts.add(ownerContact);
        contacts.add(emergencyContact);
        return contacts;
    }
}

