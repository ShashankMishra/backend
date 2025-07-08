package com.qrust.user.api.dto;

import com.qrust.common.domain.QRDetails;
import com.qrust.common.domain.QRType;
import lombok.Data;

import java.util.UUID;

@Data
public class QRCodePublicResponse {
    private UUID id;
    private QRType type;
    private QRDetails details;
}

