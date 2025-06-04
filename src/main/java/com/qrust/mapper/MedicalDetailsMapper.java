package com.qrust.mapper;

import com.qrust.api.dto.MedicalDetailsDto;
import com.qrust.domain.MedicalDetails;

import java.util.Optional;

public class MedicalDetailsMapper {
    private MedicalDetailsMapper() {
    }

    public static Optional<MedicalDetails> map(Optional<MedicalDetailsDto> dtoOptional) {
        // map if exists, otherwise return null
        if (dtoOptional.isEmpty()) return null;
        var md = new MedicalDetails();
        var dto = dtoOptional.get();
        md.setBloodGroup(dto.getBloodGroup());
        md.setAllergies(dto.getAllergies());
        md.setCurrentMedications(dto.getCurrentMedications());
        md.setMedicalHistory(dto.getMedicalHistory());
        return Optional.of(md);
    }
}

