package com.qrust.common.repository;

import com.qrust.common.domain.CallHistory;

import java.util.List;

public interface CallHistoryRepository {
    void save(CallHistory callHistory);

    List<CallHistory> findByQrId(String qrId);

    List<CallHistory> findByContactNumber(String contactNumber);
}
