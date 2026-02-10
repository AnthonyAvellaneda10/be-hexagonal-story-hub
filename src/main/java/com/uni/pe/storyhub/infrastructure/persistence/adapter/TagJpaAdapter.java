package com.uni.pe.storyhub.infrastructure.persistence.adapter;

import com.uni.pe.storyhub.domain.entity.Tag;
import com.uni.pe.storyhub.domain.repository.TagRepository;
import com.uni.pe.storyhub.infrastructure.persistence.mapper.TagPersistenceMapper;
import com.uni.pe.storyhub.infrastructure.persistence.repository.JpaTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TagJpaAdapter implements TagRepository {

    private final JpaTagRepository jpaTagRepository;

    @Override
    public Optional<Tag> findByNombre(String nombre) {
        return jpaTagRepository.findByNombre(nombre).map(TagPersistenceMapper::toDomain);
    }

    @Override
    public Tag save(Tag tag) {
        return TagPersistenceMapper.toDomain(
                jpaTagRepository.save(TagPersistenceMapper.toEntity(tag)));
    }

    @Override
    public List<Tag> findAll() {
        return jpaTagRepository.findAll().stream()
                .map(TagPersistenceMapper::toDomain)
                .toList();
    }
}
