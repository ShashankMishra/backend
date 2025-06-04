package com.qrust.mapper;

import com.qrust.api.dto.LuggageDetailsDto;
import com.qrust.api.dto.ContactDto;
import com.qrust.domain.LuggageDetails;
import com.qrust.domain.Contact;

public class LuggageDetailsMapper implements QRDetailsMapper<LuggageDetailsDto, LuggageDetails> {
    @Override
    public LuggageDetails toEntity(LuggageDetailsDto dto) {
        if (dto == null) return null;
        LuggageDetails ld = new LuggageDetails();
        ld.setDescription(dto.getDescription());
        ld.setOwnerContact(ContactMapper.map(dto.getOwnerContact()));
        ld.setEmergencyContact(ContactMapper.map(dto.getEmergencyContact()));
        return ld;
    }

}

