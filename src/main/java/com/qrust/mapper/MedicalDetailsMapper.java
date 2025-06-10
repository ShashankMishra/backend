package com.qrust.mapper;

import com.qrust.api.dto.MedicalDetailsDto;
import com.qrust.domain.MedicalDetails;

import java.util.Optional;

public class MedicalDetailsMapper {
    private MedicalDetailsMapper() {
    }

    public static MedicalDetails map(MedicalDetailsDto medicalDetailsDto) {
        // map if exists, otherwise return null
        if (medicalDetailsDto == null) return null;
        var medicalDetails = new MedicalDetails();
        medicalDetails.setBloodGroup(medicalDetailsDto.getBloodGroup());
        medicalDetails.setAllergies(medicalDetailsDto.getAllergies());
        medicalDetails.setCurrentMedications(medicalDetailsDto.getCurrentMedications());
        medicalDetails.setMedicalHistory(medicalDetailsDto.getMedicalHistory());
        return medicalDetails;
    }
}

