package com.uni.pe.storyhub.domain.repository;

import com.uni.pe.storyhub.domain.entity.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, Integer> {
    Optional<UserSession> findByRefreshToken(String refreshToken);

    @Modifying
    @Transactional
    void deleteByUser_IdUsuario(Integer idUsuario);
}
