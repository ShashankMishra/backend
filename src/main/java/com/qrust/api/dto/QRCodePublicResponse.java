package com.qrust.api.dto;

import com.qrust.domain.QRDetails;
import com.qrust.domain.QRType;
import lombok.Data;

import java.util.UUID;

@Data
public class QRCodePublicResponse {
    private UUID id;
    private QRType type;
    private QRDetails details;
}

