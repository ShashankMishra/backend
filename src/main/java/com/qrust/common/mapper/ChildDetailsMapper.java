package com.qrust.common.mapper;

import com.qrust.common.domain.ChildDetails;
import com.qrust.user.api.dto.ChildDetailsDto;

public class ChildDetailsMapper implements QRDetailsMapper<ChildDetailsDto, ChildDetails> {
    @Override
    public ChildDetails toEntity(ChildDetailsDto dto) {
        if (dto == null) return null;
        ChildDetails cd = new ChildDetails();
        cd.setFullName(dto.getFullName());
        cd.setSchoolName(dto.getSchoolName());
        cd.setEmergencyContact(ContactMapper.map(dto.getEmergencyContact()));
        cd.setMedicalDetails(MedicalDetailsMapper.map(dto.getMedicalDetails()));
        return cd;
    }

}

