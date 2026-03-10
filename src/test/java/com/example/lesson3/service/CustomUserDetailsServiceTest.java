package com.example.lesson3.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.example.lesson3.model.CustomUserDetails;
import com.example.lesson3.model.User;
import com.example.lesson3.repository.UserRepository;

import java.util.Optional;

@SpringBootTest
public class CustomUserDetailsServiceTest {

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    private User user;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password123");
    }

    @Test
    public void testLoadUserByUsername_Success() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        CustomUserDetails userDetails = (CustomUserDetails) customUserDetailsService.loadUserByUsername("test@example.com");

        assertNotNull(userDetails);
        assertEquals("test@example.com", userDetails.getUsername());
    }

    @Test
    public void testLoadUserByUsername_UserNotFound() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            customUserDetailsService.loadUserByUsername("test@example.com");
        });
    }
}
