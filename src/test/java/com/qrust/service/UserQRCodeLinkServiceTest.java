package com.qrust.service;

import com.qrust.domain.UserQRCodeLink;
import com.qrust.repository.UserQRCodeLinkRepository;
import com.qrust.service.impl.UserQRCodeLinkServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserQRCodeLinkServiceTest {
    private UserQRCodeLinkRepository linkRepository;
    private UserQRCodeLinkService linkService;

    @BeforeEach
    void setUp() {
        linkRepository = Mockito.mock(UserQRCodeLinkRepository.class);
        linkService = new UserQRCodeLinkServiceImpl(linkRepository);
    }

    @Test
    void testLinkQrToProfile() {
        doNothing().when(linkRepository).save(any(UserQRCodeLink.class));
        linkService.linkQrToProfile(UUID.randomUUID(), UUID.randomUUID(), "user1");
        verify(linkRepository, times(1)).save(any(UserQRCodeLink.class));
    }

    @Test
    void testGetLinkedQrCodes() {
        when(linkRepository.findByOwnerId("user1")).thenReturn(List.of(new UserQRCodeLink()));
        List<UserQRCodeLink> links = linkService.getLinkedQrCodes("user1");
        assertEquals(1, links.size());
    }
}

