package com.qrust.common.mapper;

import com.qrust.common.domain.Contact;
import com.qrust.user.api.dto.ContactDto;

public class ContactMapper {

    private ContactMapper() {}

    public static Contact map(ContactDto contactDto){
        if (contactDto == null) return null;
        Contact contact = new Contact();
        contact.setPhoneNumber(contactDto.getPhoneNumber());
        contact.setPreference(mapContactPreferences(contactDto.getPreference()));
        return contact;
    }

    private static com.qrust.common.domain.ContactPreference mapContactPreferences(com.qrust.user.api.dto.ContactPreference contactPreferenceDto){
        if (contactPreferenceDto == null) return null;
        com.qrust.common.domain.ContactPreference contactPreference = new com.qrust.common.domain.ContactPreference();
        contactPreference.setContactPolicy(contactPreferenceDto.getContactPolicy());
        contactPreference.setCustomMessage(contactPreferenceDto.getCustomMessage());
        return contactPreference;
    }
}
