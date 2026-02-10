package com.uni.pe.storyhub.application.mapper;

import com.uni.pe.storyhub.application.dto.request.RegisterRequest;
import com.uni.pe.storyhub.domain.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AuthMapperTest {

    @Autowired
    private AuthMapper authMapper;

    @Test
    void toEntity_ShouldMapCorrectly() {
        RegisterRequest request = new RegisterRequest();
        request.setNombreCompleto("Juan Perez");
        request.setEmail("juan@test.com");
        request.setUsername("juanp");
        request.setPassword("pass123");

        User entity = authMapper.toEntity(request);

        assertNotNull(entity);
        assertEquals(request.getNombreCompleto(), entity.getNombreCompleto());
        assertEquals(request.getEmail(), entity.getEmail());
        assertEquals(request.getUsername(), entity.getUsername());
        assertEquals(request.getPassword(), entity.getPassword());
    }
}
