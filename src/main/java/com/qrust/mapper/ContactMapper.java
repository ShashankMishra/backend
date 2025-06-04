package com.qrust.mapper;

import com.qrust.api.dto.ContactDto;
import com.qrust.domain.Contact;

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
