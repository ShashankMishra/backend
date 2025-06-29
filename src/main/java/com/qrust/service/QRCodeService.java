package com.qrust.service;

import com.qrust.api.dto.QRCodePublicResponse;
import com.qrust.api.dto.QRCodeRequest;
import com.qrust.api.dto.QRCodeResponse;
import com.qrust.domain.QRCode;
import com.qrust.exceptions.LimitReached;

import java.util.List;
import java.util.UUID;

public interface QRCodeService {
    QRCode createQr(QRCodeRequest req) throws LimitReached;
    QRCode updateQr(UUID qrId, QRCodeRequest req);
    void deleteQr(UUID qrId);
    List<QRCode> getAllQrs();
    QRCode getQr(UUID id);

    QRCodeResponse toResponse(QRCode entity);
    QRCodePublicResponse toPublicResponse(QRCode entity);

    QRCode updateIsPublic(UUID id, boolean b);
}

