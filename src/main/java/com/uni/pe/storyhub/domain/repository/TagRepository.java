package com.uni.pe.storyhub.domain.repository;

import com.uni.pe.storyhub.domain.entity.Tag;
import java.util.Optional;
import java.util.List;

public interface TagRepository {
    Optional<Tag> findByNombre(String nombre);

    Tag save(Tag tag);

    List<Tag> findAll();
}
