package com.uni.pe.storyhub.application.usecase;

import com.uni.pe.storyhub.application.mapper.AuthMapper;
import com.uni.pe.storyhub.application.mapper.BlogMapper;
import com.uni.pe.storyhub.application.mapper.CommentMapper;
import com.uni.pe.storyhub.domain.repository.*;
import com.uni.pe.storyhub.domain.repository.CommentRepository;
import com.uni.pe.storyhub.infrastructure.util.ToastIdGenerator;
import com.uni.pe.storyhub.domain.port.out.StoragePort;
import com.uni.pe.storyhub.domain.repository.LikeByUserRepository;
import com.uni.pe.storyhub.application.util.ImageValidator;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class UsecaseBoilerplateTest {
        @Test
        void testConstructors() {
                assertNotNull(new BlogUseCase(mock(BlogRepository.class), mock(TagRepository.class),
                                mock(UserRepository.class),
                                mock(BlogVistaRepository.class), mock(LikeByUserRepository.class),
                                mock(BlogMapper.class),
                                mock(ToastIdGenerator.class),
                                mock(StoragePort.class),
                                mock(ImageValidator.class)));
                assertNotNull(new CommentUseCase(mock(CommentRepository.class), mock(BlogRepository.class),
                                mock(UserRepository.class), mock(CommentMapper.class), mock(ToastIdGenerator.class)));
                assertNotNull(new UserUseCase(mock(UserRepository.class), mock(BlogRepository.class),
                                mock(AuthMapper.class),
                                mock(BlogMapper.class), mock(ToastIdGenerator.class), mock(StoragePort.class),
                                mock(ImageValidator.class)));
        }
}
