package com.qrust.mapper;

import com.qrust.api.dto.VehicleDetailsDto;
import com.qrust.domain.VehicleDetails;

public class VehicleDetailsMapper implements QRDetailsMapper<VehicleDetailsDto, VehicleDetails> {
    @Override
    public VehicleDetails toEntity(VehicleDetailsDto dto) {
        if (dto == null) return null;
        VehicleDetails vd = new VehicleDetails();
        vd.setNumberPlate(dto.getNumberPlate());
        vd.setModelDescription(dto.getModelDescription());
        vd.setOwnerContact(ContactMapper.map(dto.getOwnerContact()));
        vd.setEmergencyContact(ContactMapper.map(dto.getEmergencyContact()));
        return vd;
    }

}

