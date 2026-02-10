package com.uni.pe.storyhub.infrastructure.persistence.repository;

import com.uni.pe.storyhub.infrastructure.persistence.entity.UserSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Repository
public interface JpaUserSessionRepository extends JpaRepository<UserSessionEntity, Integer> {
    Optional<UserSessionEntity> findByRefreshToken(String refreshToken);

    @Modifying
    @Transactional
    void deleteByUser_IdUsuario(Integer idUsuario);
}
