package com.qrust.service.impl;

import com.qrust.domain.UserQRCodeLink;
import com.qrust.repository.UserQRCodeLinkRepository;
import com.qrust.service.UserQRCodeLinkService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class UserQRCodeLinkServiceImpl implements UserQRCodeLinkService {
    private final UserQRCodeLinkRepository linkRepository;

    @Inject
    public UserQRCodeLinkServiceImpl(UserQRCodeLinkRepository linkRepository) {
        this.linkRepository = linkRepository;
    }

    @Override
    public UserQRCodeLink activateQr(String activationCode, String userId) {
        // Simplified: find by activationCode (not implemented), set ownerId, linkedAt
        // In real code, you would index by activationCode
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void linkQrToProfile(UUID qrId, UUID profileId, String userId) {
        UserQRCodeLink link = new UserQRCodeLink();
        link.setLinkId(UUID.randomUUID());
        link.setQrId(qrId);
        link.setOwnerId(userId);
        link.setProfileId(profileId);
        link.setLinkedAt(LocalDateTime.now());
        linkRepository.save(link);
    }

    @Override
    public List<UserQRCodeLink> getLinkedQrCodes(String userId) {
        return linkRepository.findByOwnerId(userId);
    }
}

