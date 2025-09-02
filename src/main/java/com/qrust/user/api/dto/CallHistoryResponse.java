package com.qrust.user.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class CallHistoryResponse {
    private String qrId;
    private Instant timestamp;
    private String contactNumber;
    private String callFrom;
}
