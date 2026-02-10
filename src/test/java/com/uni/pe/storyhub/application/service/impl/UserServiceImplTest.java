package com.uni.pe.storyhub.application.service.impl;

import com.uni.pe.storyhub.application.dto.request.UpdateUserRequest;
import com.uni.pe.storyhub.application.dto.response.ApiResponse;
import com.uni.pe.storyhub.application.dto.response.BlogResponse;
import com.uni.pe.storyhub.application.dto.response.PublicUserProfileResponse;
import com.uni.pe.storyhub.application.dto.response.PublicUserResponse;
import com.uni.pe.storyhub.application.dto.response.UserResponse;
import com.uni.pe.storyhub.application.mapper.AuthMapper;
import com.uni.pe.storyhub.application.mapper.BlogMapper;
import com.uni.pe.storyhub.domain.entity.Blog;
import com.uni.pe.storyhub.domain.entity.User;
import com.uni.pe.storyhub.domain.repository.BlogRepository;
import com.uni.pe.storyhub.domain.repository.UserRepository;
import com.uni.pe.storyhub.infrastructure.exception.BusinessException;
import com.uni.pe.storyhub.infrastructure.util.ToastIdGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private BlogRepository blogRepository;
    @Mock
    private AuthMapper authMapper;
    @Mock
    private BlogMapper blogMapper;
    @Mock
    private ToastIdGenerator toastIdGenerator;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .idUsuario(1)
                .username("testuser")
                .nombreCompleto("Test User")
                .build();
    }

    @Test
    void getProfile_Success() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(authMapper.toUserResponse(any())).thenReturn(new UserResponse());
        when(toastIdGenerator.nextId()).thenReturn(1);

        ApiResponse<UserResponse> response = userService.getProfile("test@test.com");

        assertNotNull(response);
        assertEquals(200, response.getStatusCode());
        verify(userRepository).findByEmail("test@test.com");
    }

    @Test
    void getProfile_NotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> userService.getProfile("none@test.com"));
    }

    @Test
    void updateProfile_Success() {
        UpdateUserRequest request = UpdateUserRequest.builder()
                .username("newusername")
                .descripcion("New desc")
                .linkedin("li")
                .telegram("tg")
                .instagram("ig")
                .facebook("fb")
                .youtube("yt")
                .build();

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(authMapper.toUserResponse(any())).thenReturn(new UserResponse());
        when(toastIdGenerator.nextId()).thenReturn(1);

        ApiResponse<UserResponse> response = userService.updateProfile("test@test.com", request);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode());
        assertEquals("newusername", user.getUsername());
        assertEquals("New desc", user.getDescripcion());
        verify(userRepository).save(user);
    }

    @Test
    void updateProfile_UsernameConflict() {
        UpdateUserRequest request = UpdateUserRequest.builder()
                .username("taken")
                .build();

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(userRepository.existsByUsername("taken")).thenReturn(true);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> userService.updateProfile("test@test.com", request));
        assertEquals("El nombre de usuario ya está en uso. Por favor, elige otro.", exception.getMessage());
    }

    @Test
    void updateProfile_UsernameUnchanged() {
        UpdateUserRequest request = UpdateUserRequest.builder()
                .username("testuser") // same as setup
                .build();

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(authMapper.toUserResponse(any())).thenReturn(new UserResponse());
        when(toastIdGenerator.nextId()).thenReturn(1);

        ApiResponse<UserResponse> response = userService.updateProfile("test@test.com", request);

        assertEquals(200, response.getStatusCode());
        verify(userRepository, never()).existsByUsername(anyString());
    }

    @Test
    void updateProfile_NullUsername() {
        UpdateUserRequest request = UpdateUserRequest.builder()
                .username(null)
                .build();

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(authMapper.toUserResponse(any())).thenReturn(new UserResponse());
        when(toastIdGenerator.nextId()).thenReturn(1);

        ApiResponse<UserResponse> response = userService.updateProfile("test@test.com", request);

        assertEquals(200, response.getStatusCode());
        verify(userRepository, never()).existsByUsername(anyString());
    }

    @Test
    void getPublicProfile_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Blog> blogPage = new PageImpl<>(Collections.singletonList(new Blog()));

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(authMapper.toPublicUserResponse(any()))
                .thenReturn(PublicUserResponse.builder().username("testuser").build());
        when(blogRepository.findByAuthor_UsernameAndPublicadoTrueAndRemovedFalse(anyString(), any()))
                .thenReturn(blogPage);
        when(blogMapper.toResponse(any())).thenReturn(new BlogResponse());
        when(toastIdGenerator.nextId()).thenReturn(1);

        ApiResponse<PublicUserProfileResponse> response = userService.getPublicProfile("testuser", pageable);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode());
        assertEquals("testuser", response.getData().getUser().getUsername());
        assertNotNull(response.getData().getBlogs());
        assertEquals(1, response.getData().getBlogs().getTotalElements());
    }

    @Test
    void getPublicProfile_NotFound() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        PageRequest pageRequest = PageRequest.of(0, 10);
        assertThrows(BusinessException.class,
                () -> userService.getPublicProfile("none", pageRequest));
    }
}
