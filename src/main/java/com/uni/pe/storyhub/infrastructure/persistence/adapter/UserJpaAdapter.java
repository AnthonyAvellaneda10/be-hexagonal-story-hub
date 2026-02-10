package com.uni.pe.storyhub.infrastructure.persistence.adapter;

import com.uni.pe.storyhub.domain.entity.User;
import com.uni.pe.storyhub.domain.repository.UserRepository;
import com.uni.pe.storyhub.infrastructure.persistence.mapper.UserPersistenceMapper;
import com.uni.pe.storyhub.infrastructure.persistence.repository.JpaUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserJpaAdapter implements UserRepository {

    private final JpaUserRepository jpaUserRepository;

    @Override
    public Optional<User> findById(Integer id) {
        return jpaUserRepository.findById(id).map(UserPersistenceMapper::toDomain);
    }

    @Override
    public User save(User user) {
        return UserPersistenceMapper.toDomain(jpaUserRepository.save(UserPersistenceMapper.toEntity(user)));
    }

    @Override
    public List<User> findAll() {
        return jpaUserRepository.findAll().stream()
                .map(UserPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaUserRepository.findByEmail(email).map(UserPersistenceMapper::toDomain);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return jpaUserRepository.findByUsername(username).map(UserPersistenceMapper::toDomain);
    }

    @Override
    public Optional<User> findByCodigoVerificacion(String codigoVerificacion) {
        return jpaUserRepository.findByCodigoVerificacion(codigoVerificacion).map(UserPersistenceMapper::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaUserRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByUsername(String username) {
        return jpaUserRepository.existsByUsername(username);
    }
}
