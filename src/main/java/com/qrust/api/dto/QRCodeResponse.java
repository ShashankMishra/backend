package com.qrust.api.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
public class QRCodeResponse {
    private UUID id;
    private String type;
    private Map<String, String> details;
    private String planType;
    private String status;
    private LocalDateTime createdAt;
}

