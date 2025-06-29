package com.qrust.api.dto;

import com.qrust.domain.QRDetails;
import com.qrust.domain.QRStatus;
import com.qrust.domain.QRType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class QRCodeResponse {
    private UUID id;
    private QRType type;
    private QRStatus status;
    private LocalDateTime createdAt;
    private QRDetails details;
    private boolean isPublic;
}
