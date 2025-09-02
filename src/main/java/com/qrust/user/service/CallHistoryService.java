package com.qrust.user.service;

import com.qrust.common.domain.CallHistory;
import com.qrust.common.domain.QRCode;
import com.qrust.common.domain.User;
import com.qrust.common.repository.CallHistoryRepository;
import com.qrust.user.api.dto.CallHistoryResponse;
import io.quarkus.security.UnauthorizedException;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
@RequiredArgsConstructor
public class CallHistoryService {

    private final CallHistoryRepository callHistoryRepository;
    private final UserService userService;
    private final QRCodeService qrCodeService;

    public void save(CallHistory callHistory) {
        callHistoryRepository.save(callHistory);
    }

    public List<CallHistoryResponse> findByQrId(UUID qrId) {
        QRCode existing = qrCodeService.getQr(qrId);
        if (existing == null) return new ArrayList<>();

        User currentUser = userService.getCurrentUser();
        if (existing.getOwner() == null || !existing.getOwner().getUserId().equals(currentUser.getUserId())) {
            throw new UnauthorizedException("You do not have permission to view call history for this QR code.");
        }
        List<CallHistory> callHistory = callHistoryRepository.findByQrId(qrId.toString());

        return callHistory.stream().map(h -> {
            String callFrom = h.getCallFrom();
            if (callFrom != null && callFrom.length() > 4) {
                callFrom = callFrom.substring(0, callFrom.length() - 4) + "****";
            }
            return new CallHistoryResponse(
                    h.getQrId(),
                    h.getTimestamp(),
                    h.getContactNumber(),
                    callFrom
            );
        }).toList();
    }

    public List<CallHistory> findByContactNumber(String contactNumber) {
        return callHistoryRepository.findByContactNumber(contactNumber);
    }
}
