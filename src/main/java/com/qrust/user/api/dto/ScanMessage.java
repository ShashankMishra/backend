package com.qrust.user.api.dto;

import com.qrust.common.domain.QRCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScanMessage {
    private QRCode qrCode;
    private String ownerName;
    private UUID scanId;
    private int retryCount;
}