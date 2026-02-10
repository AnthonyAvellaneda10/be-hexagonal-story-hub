package com.uni.pe.storyhub.application.service.impl;

import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceImplTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailServiceImpl emailService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(emailService, "fromEmail", "from@test.com");
    }

    @Test
    void sendVerificationEmail_Success() {
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.sendVerificationEmail("to@test.com", "123456");

        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    void sendResetPasswordEmail_Success() {
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.sendResetPasswordEmail("to@test.com", "123456");

        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    void sendStyledEmail_ErrorPath() {
        // Simulate exception during creating or sending
        when(mailSender.createMimeMessage()).thenThrow(new RuntimeException("Mail error"));

        emailService.sendVerificationEmail("to@test.com", "123456");

        // Should log error but not throw exception
        verify(mailSender, never()).send(any(MimeMessage.class));
    }
}
