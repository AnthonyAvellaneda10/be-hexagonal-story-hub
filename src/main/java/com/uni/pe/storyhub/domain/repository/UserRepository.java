package com.uni.pe.storyhub.domain.repository;

import com.uni.pe.storyhub.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    Optional<User> findByCodigoVerificacion(String codigoVerificacion);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);
}
