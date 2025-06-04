package com.qrust.mapper;

import com.qrust.api.dto.QRDetailsDto;
import com.qrust.domain.QRDetails;

public interface QRDetailsMapper<D extends QRDetailsDto, E extends QRDetails> {
    E toEntity(D dto);
}

