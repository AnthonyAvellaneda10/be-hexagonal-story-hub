package com.uni.pe.storyhub.domain.repository;

import com.uni.pe.storyhub.domain.entity.User;
import java.util.Optional;
import java.util.List;

public interface UserRepository {
    Optional<User> findById(Integer id);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByCodigoVerificacion(String codigo);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    User save(User user);

    List<User> findAll();
}
