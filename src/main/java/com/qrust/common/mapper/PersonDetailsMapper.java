package com.qrust.common.mapper;

import com.qrust.common.domain.PersonDetails;
import com.qrust.user.api.dto.PersonDetailsDto;

public class PersonDetailsMapper implements QRDetailsMapper<PersonDetailsDto, PersonDetails> {
    @Override
    public PersonDetails toEntity(PersonDetailsDto dto) {
        if (dto == null) return null;
        PersonDetails pd = new PersonDetails();
        pd.setFullName(dto.getFullName());
        pd.setEmergencyContact(ContactMapper.map(dto.getEmergencyContact()));
        pd.setMedicalDetails(MedicalDetailsMapper.map(dto.getMedicalDetails()));
        return pd;
    }
}

