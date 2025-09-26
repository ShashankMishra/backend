package com.qrust.common.mapper;

import com.qrust.common.domain.Contact;
import com.qrust.user.api.dto.ContactDto;

public class ContactMapper {

    private ContactMapper() {
    }

    public static Contact map(ContactDto contactDto) {
        if (contactDto == null) return null;
        return Contact.builder().phoneNumber(contactDto.getPhoneNumber()).preference(mapContactPreferences(contactDto.getPreference())).build();
    }

    private static com.qrust.common.domain.ContactPreference mapContactPreferences(com.qrust.user.api.dto.ContactPreference contactPreferenceDto) {
        if (contactPreferenceDto == null) return null;
        com.qrust.common.domain.ContactPreference contactPreference = new com.qrust.common.domain.ContactPreference();
        contactPreference.setContactPolicy(contactPreferenceDto.getContactPolicy());
        return contactPreference;
    }
}
