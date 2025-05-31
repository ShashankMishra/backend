package com.qrust.repository;

import com.qrust.domain.UserProfile;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserProfileRepository {
    void save(UserProfile profile);
    Optional<UserProfile> findByProfileId(UUID profileId);
    List<UserProfile> findByOwnerId(String ownerId);
    void delete(UUID profileId);
}

