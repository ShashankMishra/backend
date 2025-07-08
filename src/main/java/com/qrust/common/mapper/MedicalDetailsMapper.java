package com.qrust.common.mapper;

import com.qrust.common.domain.MedicalDetails;
import com.qrust.user.api.dto.MedicalDetailsDto;

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

