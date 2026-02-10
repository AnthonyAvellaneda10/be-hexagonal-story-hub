package com.uni.pe.storyhub.application.usecase;

import com.uni.pe.storyhub.application.port.in.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailUseCase implements EmailService {

    private final JavaMailSender mailSender;

    @org.springframework.beans.factory.annotation.Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public void sendVerificationEmail(String to, String code) {
        log.info("Enviando HTML de verificación a {}: {}", to, code);
        sendStyledEmail(EmailDetails.builder()
                .to(to)
                .subject("Verificación de Correo - Story-Hub")
                .title("Verifica tu cuenta")
                .description(
                        "Hemos recibido un intento de registro. Utiliza el siguiente código para completar la verificación de tu cuenta en Story-Hub.")
                .code(code)
                .primaryColor("#7C3AED")
                .codeBgColor("#F3F4F6")
                .codeTextColor("#1F2937")
                .senderType("Story Hub Notifications")
                .footerNote("© 2026 Story-Hub. Todos los derechos reservados.")
                .build());
    }

    @Override
    public void sendResetPasswordEmail(String to, String code) {
        log.info("Enviando HTML de reseteo de contraseña a {}: {}", to, code);
        sendStyledEmail(EmailDetails.builder()
                .to(to)
                .subject("Restablecer Contraseña - Story-Hub")
                .title("Recuperación de Contraseña")
                .description(
                        "¿Olvidaste tu contraseña? No te preocupes. Utiliza el siguiente código para restablecer tu acceso seguro a Story-Hub.")
                .code(code)
                .primaryColor("#DC2626")
                .codeBgColor("#FEF2F2")
                .codeTextColor("#DC2626")
                .senderType("Story Hub Security")
                .footerNote("© 2026 Story-Hub. Soporte de Seguridad.")
                .build());
    }

    private void sendStyledEmail(EmailDetails details) {
        try {
            jakarta.mail.internet.MimeMessage mimeMessage = mailSender.createMimeMessage();
            org.springframework.mail.javamail.MimeMessageHelper helper = new org.springframework.mail.javamail.MimeMessageHelper(
                    mimeMessage, "utf-8");

            String htmlContent = "<div style=\"background-color: #F8F9FC; padding: 50px 0; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;\">"
                    + "    <div style=\"max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 20px; box-shadow: 0 4px 15px rgba(0,0,0,0.05); overflow: hidden; border: 1px solid #E5E7EB;\">"
                    + "        <div style=\"padding: 40px; text-align: center;\">"
                    + "            <div style=\"margin-bottom: 30px;\">"
                    + "                <span style=\"font-size: 28px; font-weight: 800; color: "
                    + details.getPrimaryColor()
                    + "; letter-spacing: -1px;\">Story<span style=\"color: #1F2937;\">Hub</span></span>"
                    + "            </div>"
                    + "            <h1 style=\"font-size: 24px; font-weight: 700; color: #1F2937; margin: 0 0 15px 0;\">"
                    + details.getTitle() + "</h1>"
                    + "            <p style=\"font-size: 16px; color: #4B5563; line-height: 1.5; margin-bottom: 30px;\">"
                    + details.getDescription()
                    + "            </p>"
                    + "            <div style=\"background-color: " + details.getCodeBgColor()
                    + "; border-radius: 12px; padding: 30px; margin-bottom: 30px;\">"
                    + "                <span style=\"font-size: 42px; font-weight: 800; color: "
                    + details.getCodeTextColor()
                    + "; letter-spacing: 12px; font-family: monospace;\">"
                    + details.getCode() + "</span>"
                    + "            </div>"
                    + "            <p style=\"font-size: 14px; color: #6B7280; margin: 0;\">"
                    + "                Si no solicitaste este cambio, puedes ignorar este correo con seguridad. El código expirará pronto."
                    + "            </p>"
                    + "        </div>"
                    + "        <div style=\"background-color: #F9FAFB; padding: 20px; text-align: center; border-top: 1px solid #E5E7EB;\">"
                    + "            <p style=\"font-size: 14px; color: #9CA3AF; margin: 0;\">" + details.getFooterNote()
                    + "</p>"
                    + "        </div>"
                    + "    </div>"
                    + "</div>";

            helper.setText(htmlContent, true);
            helper.setTo(details.getTo());
            helper.setSubject(details.getSubject());
            helper.setFrom(fromEmail, details.getSenderType());

            mailSender.send(mimeMessage);
        } catch (Exception e) {
            log.error("Error al enviar email styled ({}): {}", details.getSubject(), e.getMessage());
        }
    }

    @lombok.Builder
    @lombok.Getter
    private static class EmailDetails {
        private String to;
        private String subject;
        private String title;
        private String description;
        private String code;
        private String primaryColor;
        private String codeBgColor;
        private String codeTextColor;
        private String senderType;
        private String footerNote;
    }
}
