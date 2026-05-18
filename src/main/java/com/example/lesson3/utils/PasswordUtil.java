package com.example.lesson3.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordUtil {
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public static String hashPassword(String password) {
        if (password == null || password.isEmpty()) {
            return null;
        }
        return encoder.encode(password);
    }

    public static boolean checkPassword(String password, String hashedPassword) {
        if (password == null || hashedPassword == null) {
            return false;
        }
        return encoder.matches(password, hashedPassword);
    }
}
