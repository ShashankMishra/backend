package com.qrust.common.domain;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class PersonDetails implements QRDetails {
    private final QRType type = QRType.PERSON;
    private String fullName;
    private Contact emergencyContact;
    private MedicalDetails medicalDetails;

    @Override
    public Set<Contact> getContacts() {
        Set<Contact> contacts = new HashSet<>();
        contacts.add(emergencyContact);
        return contacts;
    }
}

