package com.uni.pe.storyhub.infrastructure.persistence.adapter;

import com.uni.pe.storyhub.domain.entity.User;
import com.uni.pe.storyhub.infrastructure.persistence.entity.UserEntity;
import com.uni.pe.storyhub.infrastructure.persistence.repository.JpaUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserJpaAdapterTest {

    @Mock
    private JpaUserRepository jpaUserRepository;

    @InjectMocks
    private UserJpaAdapter userJpaAdapter;

    private User user;
    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .idUsuario(1)
                .username("testuser")
                .email("test@test.com")
                .build();

        userEntity = UserEntity.builder()
                .idUsuario(1)
                .username("testuser")
                .email("test@test.com")
                .build();
    }

    @Test
    void findById_Success() {
        when(jpaUserRepository.findById(1)).thenReturn(Optional.of(userEntity));

        Optional<User> result = userJpaAdapter.findById(1);

        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
        verify(jpaUserRepository).findById(1);
    }

    @Test
    void save_Success() {
        when(jpaUserRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        User result = userJpaAdapter.save(user);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(jpaUserRepository).save(any(UserEntity.class));
    }

    @Test
    void findAll_Success() {
        when(jpaUserRepository.findAll()).thenReturn(Collections.singletonList(userEntity));

        List<User> result = userJpaAdapter.findAll();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("testuser", result.get(0).getUsername());
        verify(jpaUserRepository).findAll();
    }

    @Test
    void findByEmail_Success() {
        when(jpaUserRepository.findByEmail("test@test.com")).thenReturn(Optional.of(userEntity));

        Optional<User> result = userJpaAdapter.findByEmail("test@test.com");

        assertTrue(result.isPresent());
        assertEquals("test@test.com", result.get().getEmail());
    }

    @Test
    void findByUsername_Success() {
        when(jpaUserRepository.findByUsername("testuser")).thenReturn(Optional.of(userEntity));

        Optional<User> result = userJpaAdapter.findByUsername("testuser");

        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
    }

    @Test
    void findByCodigoVerificacion_Success() {
        when(jpaUserRepository.findByCodigoVerificacion("code")).thenReturn(Optional.of(userEntity));

        Optional<User> result = userJpaAdapter.findByCodigoVerificacion("code");

        assertTrue(result.isPresent());
    }

    @Test
    void existsByEmail_Success() {
        when(jpaUserRepository.existsByEmail("test@test.com")).thenReturn(true);
        assertTrue(userJpaAdapter.existsByEmail("test@test.com"));
    }

    @Test
    void existsByUsername_Success() {
        when(jpaUserRepository.existsByUsername("testuser")).thenReturn(true);
        assertTrue(userJpaAdapter.existsByUsername("testuser"));
    }
}
