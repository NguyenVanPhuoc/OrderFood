package com.example.lesson3.service;

import com.example.lesson3.model.PasswordResetToken;
import com.example.lesson3.model.User;
import com.example.lesson3.repository.PasswordResetTokenRepository;
import com.example.lesson3.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PasswordResetServiceTest {

    @Mock private PasswordResetTokenRepository tokenRepository;
    @Mock private UserRepository userRepository;
    @Mock private BCryptPasswordEncoder passwordEncoder;
    @Mock private EmailService emailService;

    @InjectMocks private PasswordResetService passwordResetService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // createAndSendResetToken — email không tồn tại → ném ngoại lệ
    @Test
    void testCreateAndSendResetToken_EmailNotFound_Throws() {
        when(userRepository.findByEmail("none@example.com")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> passwordResetService.createAndSendResetToken("none@example.com", "http://localhost"));

        verify(tokenRepository, never()).save(any());
        verify(emailService, never()).sendPasswordResetEmail(any(), any());
    }

    // createAndSendResetToken — thành công: xóa token cũ, lưu token mới, gửi email
    @Test
    void testCreateAndSendResetToken_Success_SavesTokenAndSendsEmail() {
        User user = new User(); user.setId(1L); user.setEmail("user@example.com");
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(tokenRepository.save(any(PasswordResetToken.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        passwordResetService.createAndSendResetToken("user@example.com", "http://localhost");

        verify(tokenRepository).deleteByUser(user);
        verify(tokenRepository).save(any(PasswordResetToken.class));
        verify(emailService).sendPasswordResetEmail(eq("user@example.com"), contains("/admin/reset-password?token="));
    }

    // isValidToken — token hợp lệ và chưa hết hạn → true
    @Test
    void testIsValidToken_ValidNotExpired_ReturnsTrue() {
        PasswordResetToken token = new PasswordResetToken("abc123", new User(),
                LocalDateTime.now().plusHours(1));
        when(tokenRepository.findByToken("abc123")).thenReturn(Optional.of(token));

        assertTrue(passwordResetService.isValidToken("abc123"));
    }

    // isValidToken — token không tồn tại → false
    @Test
    void testIsValidToken_TokenNotFound_ReturnsFalse() {
        when(tokenRepository.findByToken("missing")).thenReturn(Optional.empty());

        assertFalse(passwordResetService.isValidToken("missing"));
    }

    // isValidToken — token đã hết hạn → false
    @Test
    void testIsValidToken_ExpiredToken_ReturnsFalse() {
        PasswordResetToken token = new PasswordResetToken("old-token", new User(),
                LocalDateTime.now().minusMinutes(1));
        when(tokenRepository.findByToken("old-token")).thenReturn(Optional.of(token));

        assertFalse(passwordResetService.isValidToken("old-token"));
    }

    // resetPassword — token không tồn tại → ném ngoại lệ
    @Test
    void testResetPassword_TokenNotFound_Throws() {
        when(tokenRepository.findByToken("bad")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> passwordResetService.resetPassword("bad", "newpass"));

        verify(userRepository, never()).save(any());
    }

    // resetPassword — token đã hết hạn → ném ngoại lệ và xóa token
    @Test
    void testResetPassword_ExpiredToken_ThrowsAndDeletesToken() {
        PasswordResetToken token = new PasswordResetToken("expired", new User(),
                LocalDateTime.now().minusMinutes(5));
        when(tokenRepository.findByToken("expired")).thenReturn(Optional.of(token));

        assertThrows(RuntimeException.class,
                () -> passwordResetService.resetPassword("expired", "newpass"));

        verify(tokenRepository).delete(token);
        verify(userRepository, never()).save(any());
    }

    // resetPassword — thành công: mã hóa mật khẩu, lưu user, xóa token
    @Test
    void testResetPassword_ValidToken_UpdatesPasswordAndDeletesToken() {
        User user = new User(); user.setId(2L);
        PasswordResetToken token = new PasswordResetToken("valid-token", user,
                LocalDateTime.now().plusHours(1));
        when(tokenRepository.findByToken("valid-token")).thenReturn(Optional.of(token));
        when(passwordEncoder.encode("newpass123")).thenReturn("$2a$hashed");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        passwordResetService.resetPassword("valid-token", "newpass123");

        assertEquals("$2a$hashed", user.getPassword());
        verify(userRepository).save(user);
        verify(tokenRepository).delete(token);
    }
}
