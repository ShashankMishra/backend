package com.qrust.repository;

import com.qrust.domain.UserQRCodeLink;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserQRCodeLinkRepository {
    void save(UserQRCodeLink link);
    Optional<UserQRCodeLink> findByLinkId(UUID linkId);
    List<UserQRCodeLink> findByOwnerId(String ownerId);
    List<UserQRCodeLink> findByQrId(UUID qrId);
    void delete(UUID linkId);
}

