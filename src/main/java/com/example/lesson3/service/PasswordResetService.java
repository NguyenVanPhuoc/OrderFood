package com.example.lesson3.service;

import com.example.lesson3.model.PasswordResetToken;
import com.example.lesson3.model.User;
import com.example.lesson3.repository.PasswordResetTokenRepository;
import com.example.lesson3.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordResetService {

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Transactional
    public void createAndSendResetToken(String email, String baseUrl) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email không tồn tại trong hệ thống."));

        // Xóa token cũ nếu đã có
        tokenRepository.deleteByUser(user);

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken(
                token, user, LocalDateTime.now().plusHours(1)
        );
        tokenRepository.save(resetToken);

        String resetLink = baseUrl + "/admin/reset-password?token=" + token;
        emailService.sendPasswordResetEmail(email, resetLink);
    }

    public boolean isValidToken(String token) {
        Optional<PasswordResetToken> opt = tokenRepository.findByToken(token);
        return opt.isPresent() && !opt.get().isExpired();
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token không hợp lệ."));

        if (resetToken.isExpired()) {
            tokenRepository.delete(resetToken);
            throw new RuntimeException("Token đã hết hạn.");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        tokenRepository.delete(resetToken);
    }
}
