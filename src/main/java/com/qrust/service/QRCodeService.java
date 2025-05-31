package com.qrust.service;

import com.qrust.domain.QRCode;
import com.qrust.domain.PlanType;
import java.util.List;
import java.util.UUID;

public interface QRCodeService {
    List<QRCode> createQrCodes(int count, PlanType planType);
    QRCode getQrInfo(String publicToken);
    void revokeQr(UUID qrId);
}

