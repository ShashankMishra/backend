package com.qrust.user.api.dto;

import com.qrust.common.domain.QRDetails;
import com.qrust.common.domain.QRStatus;
import com.qrust.common.domain.QRType;
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
    private String shortId;
    private boolean isPremium;
    private boolean isSetupDone;
}
