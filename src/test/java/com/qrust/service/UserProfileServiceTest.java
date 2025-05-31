package com.qrust.service;

import com.qrust.domain.UserProfile;
import com.qrust.repository.UserProfileRepository;
import com.qrust.service.impl.UserProfileServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserProfileServiceTest {
    private UserProfileRepository userProfileRepository;
    private UserProfileService userProfileService;

    @BeforeEach
    void setUp() {
        userProfileRepository = Mockito.mock(UserProfileRepository.class);
        userProfileService = new UserProfileServiceImpl(userProfileRepository);
    }

    @Test
    void testCreateProfile() {
        doNothing().when(userProfileRepository).save(any(UserProfile.class));
        UserProfile profile = new UserProfile();
        UserProfile created = userProfileService.createProfile(profile);
        assertEquals(profile, created);
        verify(userProfileRepository, times(1)).save(profile);
    }

    @Test
    void testGetProfile() {
        UserProfile profile = new UserProfile();
        UUID id = UUID.randomUUID();
        when(userProfileRepository.findByProfileId(id)).thenReturn(Optional.of(profile));
        UserProfile found = userProfileService.getProfile(id, "user1");
        assertNotNull(found);
    }

    @Test
    void testGetProfilesByOwner() {
        when(userProfileRepository.findByOwnerId("user1")).thenReturn(List.of(new UserProfile()));
        List<UserProfile> profiles = userProfileService.getProfilesByOwner("user1");
        assertEquals(1, profiles.size());
    }

    @Test
    void testUpdateProfile() {
        doNothing().when(userProfileRepository).save(any(UserProfile.class));
        UserProfile profile = new UserProfile();
        UserProfile updated = userProfileService.updateProfile(profile);
        assertEquals(profile, updated);
        verify(userProfileRepository, times(1)).save(profile);
    }

    @Test
    void testDeleteProfile() {
        doNothing().when(userProfileRepository).delete(any(UUID.class));
        userProfileService.deleteProfile(UUID.randomUUID());
        verify(userProfileRepository, times(1)).delete(any(UUID.class));
    }
}

