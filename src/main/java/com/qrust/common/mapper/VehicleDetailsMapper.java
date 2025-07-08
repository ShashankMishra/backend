package com.qrust.common.mapper;

import com.qrust.common.domain.VehicleDetails;
import com.qrust.user.api.dto.VehicleDetailsDto;

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

