package com.uni.pe.storyhub.infrastructure.persistence.repository;

import com.uni.pe.storyhub.infrastructure.persistence.entity.TagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface JpaTagRepository extends JpaRepository<TagEntity, Integer> {
    Optional<TagEntity> findByNombre(String nombre);
}
