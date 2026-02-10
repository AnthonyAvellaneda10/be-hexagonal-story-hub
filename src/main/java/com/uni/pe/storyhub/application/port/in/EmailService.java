package com.uni.pe.storyhub.application.port.in;

public interface EmailService {
    void sendVerificationEmail(String to, String code);

    void sendResetPasswordEmail(String to, String code);
}
