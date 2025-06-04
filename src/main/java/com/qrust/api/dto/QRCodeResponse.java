package com.qrust.api.dto;

import com.qrust.domain.QRDetails;
import com.qrust.domain.QRType;
import com.qrust.domain.PlanType;
import com.qrust.domain.User;
import com.qrust.domain.QRStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class QRCodeResponse {
    private UUID id;
    private QRType type;
    private QRStatus status;
    private PlanType planType;
    private LocalDateTime createdAt;
    private QRDetails details;
}

