package com.uni.pe.storyhub.domain.repository;

import com.uni.pe.storyhub.domain.entity.UserSession;
import java.util.Optional;

public interface UserSessionRepository {
    Optional<UserSession> findByRefreshToken(String refreshToken);

    UserSession save(UserSession session);

    void deleteByUserId(Integer idUsuario);

    void delete(UserSession session);
}
