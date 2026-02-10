package com.uni.pe.storyhub.infrastructure.persistence.adapter;

import com.uni.pe.storyhub.domain.entity.UserSession;
import com.uni.pe.storyhub.infrastructure.persistence.entity.UserEntity;
import com.uni.pe.storyhub.infrastructure.persistence.entity.UserSessionEntity;
import com.uni.pe.storyhub.infrastructure.persistence.repository.JpaUserSessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserSessionJpaAdapterTest {

    @Mock
    private JpaUserSessionRepository jpaUserSessionRepository;

    @InjectMocks
    private UserSessionJpaAdapter userSessionJpaAdapter;

    private UserSession session;
    private UserSessionEntity sessionEntity;

    @BeforeEach
    void setUp() {
        session = UserSession.builder()
                .idSession(1)
                .refreshToken("token")
                .user(com.uni.pe.storyhub.domain.entity.User.builder().idUsuario(1).build())
                .build();

        sessionEntity = UserSessionEntity.builder()
                .idSession(1)
                .refreshToken("token")
                .user(UserEntity.builder().idUsuario(1).build())
                .build();
    }

    @Test
    void findByRefreshToken_Success() {
        when(jpaUserSessionRepository.findByRefreshToken("token")).thenReturn(Optional.of(sessionEntity));
        Optional<UserSession> result = userSessionJpaAdapter.findByRefreshToken("token");
        assertTrue(result.isPresent());
        verify(jpaUserSessionRepository).findByRefreshToken("token");
    }

    @Test
    void save_Success() {
        when(jpaUserSessionRepository.save(any(UserSessionEntity.class))).thenReturn(sessionEntity);
        UserSession result = userSessionJpaAdapter.save(session);
        assertNotNull(result);
        verify(jpaUserSessionRepository).save(any(UserSessionEntity.class));
    }

    @Test
    void deleteByUserId_Success() {
        doNothing().when(jpaUserSessionRepository).deleteByUser_IdUsuario(1);
        userSessionJpaAdapter.deleteByUserId(1);
        verify(jpaUserSessionRepository).deleteByUser_IdUsuario(1);
    }

    @Test
    void delete_Success() {
        doNothing().when(jpaUserSessionRepository).delete(any(UserSessionEntity.class));
        userSessionJpaAdapter.delete(session);
        verify(jpaUserSessionRepository).delete(any(UserSessionEntity.class));
    }
}
