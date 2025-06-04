package com.qrust.mapper;

import com.qrust.api.dto.LockscreenDetailsDto;
import com.qrust.domain.LockscreenDetails;

public class LockscreenDetailsMapper implements QRDetailsMapper<LockscreenDetailsDto, LockscreenDetails> {
    @Override
    public LockscreenDetails toEntity(LockscreenDetailsDto dto) {
        if (dto == null) return null;
        LockscreenDetails ld = new LockscreenDetails();
        ld.setDeviceName(dto.getDeviceName());
        ld.setOwnerContact(ContactMapper.map(dto.getOwnerContact()));
        ld.setEmergencyContact(ContactMapper.map(dto.getEmergencyContact()));
        ld.setMedicalDetails(MedicalDetailsMapper.map(dto.getMedicalDetails()));
        return ld;
    }
}

