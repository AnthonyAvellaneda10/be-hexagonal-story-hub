package com.uni.pe.storyhub.infrastructure.util;

import org.junit.jupiter.api.Test;
import java.util.HashSet;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class ToastIdGeneratorTest {

    private final ToastIdGenerator generator = new ToastIdGenerator();

    @Test
    void nextId_ShouldBeThreadSafeAndUnique() {
        Set<Integer> ids = new HashSet<>();
        int count = 1000;

        for (int i = 0; i < count; i++) {
            ids.add(generator.nextId());
        }

        assertEquals(count, ids.size(), "IDs should be unique");
    }

    @Test
    void nextId_ShouldIncrement() {
        int id1 = generator.nextId();
        int id2 = generator.nextId();
        assertEquals(id1 + 1, id2);
    }
}
