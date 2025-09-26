package com.qrust.common.domain;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class ChildDetails implements QRDetails {
    private final QRType type = QRType.CHILD;
    private String fullName;
    private String schoolName;
    private Contact emergencyContact;
    private MedicalDetails medicalDetails;

    @Override
    public Set<Contact> getContacts() {
        Set<Contact> contacts = new HashSet<>();
        contacts.add(emergencyContact);
        return contacts;
    }
}

