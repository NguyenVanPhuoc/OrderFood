package com.example.lesson3.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EmailServiceTest {

    @Mock private JavaMailSender mailSender;

    @InjectMocks private EmailService emailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(emailService, "fromEmail", "noreply@baofood.com");
    }

    // Gửi email thành công — mailSender.send() được gọi đúng 1 lần
    @Test
    void testSendPasswordResetEmail_Success_CallsMailSender() {
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        assertDoesNotThrow(() ->
                emailService.sendPasswordResetEmail("user@example.com", "http://localhost/reset?token=abc"));

        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    // mailSender ném exception → bọc lại thành RuntimeException
    @Test
    void testSendPasswordResetEmail_MailError_ThrowsRuntimeException() {
        doThrow(new RuntimeException("SMTP error")).when(mailSender).send(any(SimpleMailMessage.class));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                emailService.sendPasswordResetEmail("user@example.com", "http://localhost/reset?token=abc"));

        assertTrue(ex.getMessage().contains("Không thể gửi email"));
    }
}
