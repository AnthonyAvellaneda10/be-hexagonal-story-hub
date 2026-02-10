package com.uni.pe.storyhub.infrastructure.config;

import com.uni.pe.storyhub.domain.entity.User;
import com.uni.pe.storyhub.domain.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Test
    void loadUserByUsername_Success() {
        User user = new User();
        user.setEmail("test@test.com");
        user.setPassword("password");
        user.setActivo(true);
        user.setEmailVerificado(true); // Fix

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));

        UserDetails userDetails = userDetailsService.loadUserByUsername("test@test.com");

        assertNotNull(userDetails);
        assertEquals("test@test.com", userDetails.getUsername());
        verify(userRepository).findByEmail("test@test.com");
    }

    @Test
    void loadUserByUsername_UserNotFound_ThrowsException() {
        when(userRepository.findByEmail("notfound@test.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("notfound@test.com");
        });
    }

    @Test
    void loadUserByUsername_EmailNotVerified_ThrowsException() {
        User user = new User();
        user.setEmail("test@test.com");
        user.setEmailVerificado(false);

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));

        assertThrows(com.uni.pe.storyhub.infrastructure.exception.BusinessException.class, () -> {
            userDetailsService.loadUserByUsername("test@test.com");
        });
    }
}
