package com.qrust.service;

import com.qrust.domain.UserProfile;
import java.util.List;
import java.util.UUID;

public interface UserProfileService {
    UserProfile createProfile(UserProfile profile);
    UserProfile getProfile(UUID profileId, String requesterId);
    List<UserProfile> getProfilesByOwner(String ownerId);
    UserProfile updateProfile(UserProfile profile);
    void deleteProfile(UUID profileId);
}

