package com.qrust.admin.service;

import com.qrust.common.domain.QRCode;
import com.qrust.common.repository.QRCodeRepository;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@ApplicationScoped
@Slf4j
public class MigrationService {

    @Inject
    QRCodeRepository qrCodeRepository;

    private final AtomicBoolean migrationComplete = new AtomicBoolean(false);

//    @Scheduled(every = "10s", identity = "qrCodeMigrationJob")
//    public void migrateQrs() {
//        if (migrationComplete.get()) {
//            return;
//        }
//
//        log.info("Starting QR code migration scan...");
//        List<QRCode> qrsToMigrate = qrCodeRepository.findAll().stream()
//                .filter(qrCode -> qrCode.getOwner() != null && qrCode.getOwner().getUserId() != null && qrCode.getUserId() == null)
//                .toList();
//
//        if (qrsToMigrate.isEmpty()) {
//            migrationComplete.set(true);
//            log.info("Finished migration");
//            return;
//        }
//
//        qrsToMigrate.forEach(qrCode -> {
//            qrCode.setUserId(qrCode.getOwner().getUserId());
//            qrCode.setUserEmail(qrCode.getOwner().getEmail());
//            qrCodeRepository.save(qrCode);
//            log.info("Migrated QR code with id: {}", qrCode.getId());
//        });
//    }
}
