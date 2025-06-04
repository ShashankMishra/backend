package com.qrust.mapper;

import com.qrust.api.dto.ChildDetailsDto;
import com.qrust.domain.ChildDetails;

public class ChildDetailsMapper implements QRDetailsMapper<ChildDetailsDto, ChildDetails> {
    @Override
    public ChildDetails toEntity(ChildDetailsDto dto) {
        if (dto == null) return null;
        ChildDetails cd = new ChildDetails();
        cd.setFullName(dto.getFullName());
        cd.setSchoolName(dto.getSchoolName());
        cd.setGuardianContact(ContactMapper.map(dto.getGuardianContact()));
        cd.setEmergencyContact(ContactMapper.map(dto.getEmergencyContact()));
        cd.setMedicalDetails(MedicalDetailsMapper.map(dto.getMedicalDetails()));
        return cd;
    }

}

