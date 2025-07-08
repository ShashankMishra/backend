package com.qrust.common.mapper;

import com.qrust.common.domain.LuggageDetails;
import com.qrust.user.api.dto.LuggageDetailsDto;

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

