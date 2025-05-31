package com.qrust.service;

import com.qrust.domain.UserQRCodeLink;
import java.util.List;
import java.util.UUID;

public interface UserQRCodeLinkService {
    UserQRCodeLink activateQr(String activationCode, String userId);
    void linkQrToProfile(UUID qrId, UUID profileId, String userId);
    List<UserQRCodeLink> getLinkedQrCodes(String userId);
}

