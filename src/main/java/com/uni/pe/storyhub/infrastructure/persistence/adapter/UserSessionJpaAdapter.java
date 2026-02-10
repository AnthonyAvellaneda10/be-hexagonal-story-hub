package com.uni.pe.storyhub.infrastructure.persistence.adapter;

import com.uni.pe.storyhub.domain.entity.UserSession;
import com.uni.pe.storyhub.domain.repository.UserSessionRepository;
import com.uni.pe.storyhub.infrastructure.persistence.mapper.UserSessionPersistenceMapper;
import com.uni.pe.storyhub.infrastructure.persistence.repository.JpaUserSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserSessionJpaAdapter implements UserSessionRepository {

    private final JpaUserSessionRepository jpaUserSessionRepository;

    @Override
    public Optional<UserSession> findByRefreshToken(String refreshToken) {
        return jpaUserSessionRepository.findByRefreshToken(refreshToken)
                .map(UserSessionPersistenceMapper::toDomain);
    }

    @Override
    public UserSession save(UserSession session) {
        return UserSessionPersistenceMapper.toDomain(
                jpaUserSessionRepository.save(UserSessionPersistenceMapper.toEntity(session)));
    }

    @Override
    public void deleteByUserId(Integer idUsuario) {
        jpaUserSessionRepository.deleteByUser_IdUsuario(idUsuario);
    }

    @Override
    public void delete(UserSession session) {
        jpaUserSessionRepository.delete(UserSessionPersistenceMapper.toEntity(session));
    }
}
