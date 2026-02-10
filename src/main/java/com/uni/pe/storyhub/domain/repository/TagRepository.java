package com.uni.pe.storyhub.domain.repository;

import com.uni.pe.storyhub.domain.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Integer> {
    Optional<Tag> findByNombre(String nombre);
}
