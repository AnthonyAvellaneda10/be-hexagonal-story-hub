package com.uni.pe.storyhub.infrastructure.persistence.mapper;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class MapperBoilerplateTest {
    @Test
    void testConstructors() {
        assertNotNull(new UserPersistenceMapper());
        assertNotNull(new BlogPersistenceMapper());
        assertNotNull(new CommentPersistenceMapper());
        assertNotNull(new TagPersistenceMapper());
        assertNotNull(new UserSessionPersistenceMapper());
        assertNotNull(new BlogVistaPersistenceMapper());
        assertNotNull(new LikeByUserPersistenceMapper());
    }
}
