package com.qrust.service.impl;

import com.qrust.domain.UserProfile;
import com.qrust.repository.UserProfileRepository;
import com.qrust.service.UserProfileService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class UserProfileServiceImpl implements UserProfileService {
    private final UserProfileRepository userProfileRepository;

    @Inject
    public UserProfileServiceImpl(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    @Override
    public UserProfile createProfile(UserProfile profile) {
        userProfileRepository.save(profile);
        return profile;
    }

    @Override
    public UserProfile getProfile(UUID profileId, String requesterId) {
        return userProfileRepository.findByProfileId(profileId).orElse(null);
    }

    @Override
    public List<UserProfile> getProfilesByOwner(String ownerId) {
        return userProfileRepository.findByOwnerId(ownerId);
    }

    @Override
    public UserProfile updateProfile(UserProfile profile) {
        userProfileRepository.save(profile);
        return profile;
    }

    @Override
    public void deleteProfile(UUID profileId) {
        userProfileRepository.delete(profileId);
    }
}

