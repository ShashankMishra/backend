package com.qrust.common.domain;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class LuggageDetails implements QRDetails {
    private final QRType type = QRType.LUGGAGE;
    private String description;
    private Contact ownerContact;
    private Contact emergencyContact;

    @Override
    public Set<Contact> getContacts() {
        Set<Contact> contacts = new HashSet<>();
        contacts.add(ownerContact);
        contacts.add(emergencyContact);
        return contacts;
    }
}

