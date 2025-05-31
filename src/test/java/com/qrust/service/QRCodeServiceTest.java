package com.qrust.service;

import com.qrust.domain.QRCode;
import com.qrust.domain.PlanType;
import com.qrust.domain.QRStatus;
import com.qrust.repository.QRCodeRepository;
import com.qrust.service.impl.QRCodeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class QRCodeServiceTest {
    private QRCodeRepository qrCodeRepository;
    private QRCodeService qrCodeService;

    @BeforeEach
    void setUp() {
        qrCodeRepository = Mockito.mock(QRCodeRepository.class);
        qrCodeService = new QRCodeServiceImpl(qrCodeRepository);
    }

    @Test
    void testCreateQrCodes() {
        doNothing().when(qrCodeRepository).save(any(QRCode.class));
        List<QRCode> codes = qrCodeService.createQrCodes(2, PlanType.FREE);
        assertEquals(2, codes.size());
        verify(qrCodeRepository, times(2)).save(any(QRCode.class));
    }

    @Test
    void testGetQrInfo() {
        QRCode qr = new QRCode();
        qr.setPublicToken("token123");
        when(qrCodeRepository.findByPublicToken("token123")).thenReturn(Optional.of(qr));
        QRCode found = qrCodeService.getQrInfo("token123");
        assertNotNull(found);
        assertEquals("token123", found.getPublicToken());
    }

    @Test
    void testRevokeQr() {
        QRCode qr = new QRCode();
        qr.setId(UUID.randomUUID());
        qr.setStatus(QRStatus.UNASSIGNED);
        when(qrCodeRepository.findById(qr.getId())).thenReturn(Optional.of(qr));
        doNothing().when(qrCodeRepository).save(any(QRCode.class));
        qrCodeService.revokeQr(qr.getId());
        assertEquals(QRStatus.REVOKED, qr.getStatus());
    }
}

