package com.uni.pe.storyhub.infrastructure.persistence.adapter;

import com.uni.pe.storyhub.domain.entity.Tag;
import com.uni.pe.storyhub.infrastructure.persistence.entity.TagEntity;
import com.uni.pe.storyhub.infrastructure.persistence.repository.JpaTagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TagJpaAdapterTest {

    @Mock
    private JpaTagRepository jpaTagRepository;

    @InjectMocks
    private TagJpaAdapter tagJpaAdapter;

    private Tag tag;
    private TagEntity tagEntity;

    @BeforeEach
    void setUp() {
        tag = Tag.builder().idTag(1).nombre("java").build();
        tagEntity = TagEntity.builder().idTag(1).nombre("java").build();
    }

    @Test
    void findByNombre_Success() {
        when(jpaTagRepository.findByNombre("java")).thenReturn(Optional.of(tagEntity));
        Optional<Tag> result = tagJpaAdapter.findByNombre("java");
        assertTrue(result.isPresent());
        assertEquals("java", result.get().getNombre());
    }

    @Test
    void save_Success() {
        when(jpaTagRepository.save(any(TagEntity.class))).thenReturn(tagEntity);
        Tag result = tagJpaAdapter.save(tag);
        assertNotNull(result);
        assertEquals("java", result.getNombre());
    }

    @Test
    void findAll_Success() {
        when(jpaTagRepository.findAll()).thenReturn(Collections.singletonList(tagEntity));
        List<Tag> result = tagJpaAdapter.findAll();
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }
}
