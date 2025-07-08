package com.qrust.common.mapper;

import com.qrust.common.domain.Contact;
import com.qrust.user.api.dto.ContactDto;

public class ContactMapper {

    private ContactMapper() {}

    public static Contact map(ContactDto contactDto){
        if (contactDto == null) return null;
        Contact contact = new Contact();
        contact.setName(contactDto.getName());
        contact.setPhoneNumber(contactDto.getPhoneNumber());
        return contact;
    }
}
