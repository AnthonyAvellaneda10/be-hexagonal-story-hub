package com.uni.pe.storyhub;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.javamail.JavaMailSender;
import static org.mockito.Mockito.mock;

@Configuration
public class TestMailConfig {

    @Bean
    @Primary
    public JavaMailSender javaMailSender() {
        return mock(JavaMailSender.class);
    }
}
