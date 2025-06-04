package com.qrust.mapper;

import com.qrust.api.dto.PersonDetailsDto;
import com.qrust.domain.PersonDetails;

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

