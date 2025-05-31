package com.qrust.service;

import com.qrust.domain.QRCode;
import com.qrust.domain.PlanType;
import com.qrust.api.dto.QRCodeRequest;
import com.qrust.api.dto.QRCodeResponse;
import java.util.List;
import java.util.UUID;

public interface QRCodeService {
    List<QRCode> createQrCodes(int count, PlanType planType);
    QRCode getQrInfo(String publicToken);
    void revokeQr(UUID qrId);
    QRCodeResponse createQr(QRCodeRequest req);
    QRCodeResponse updateQr(UUID qrId, QRCodeRequest req);
    void deleteQr(UUID qrId);
    List<QRCode> getAllQrs();
    void linkQrToUser(UUID qrId, String userId);
    QRCodeResponse getQrWithDetailsResponse(UUID qrId);
    QRCodeResponse toResponse(QRCode entity);
}

