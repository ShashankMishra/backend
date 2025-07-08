package com.qrust.common.mapper;

import com.qrust.common.domain.QRDetails;
import com.qrust.user.api.dto.QRDetailsDto;

public interface QRDetailsMapper<D extends QRDetailsDto, E extends QRDetails> {
    E toEntity(D dto);
}

