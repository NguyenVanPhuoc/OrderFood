package com.example.lesson3.service;

import com.example.lesson3.model.User;
import com.example.lesson3.repository.UserRepository;
import com.example.lesson3.utils.PasswordUtil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpSession;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class UserServiceTest {
	@InjectMocks
    private UserService userService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private HttpSession session;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Test getAllUsers
    @Test
    void testGetAllUsers() {
        List<User> users = List.of(new User("john", "pass"), new User("jane", "pass"));
        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.getAllUsers();
        assertEquals(2, result.size());
        verify(userRepository).findAll();
    }

    // Test getUserById
    @Test
    void testGetUserById() {
        User user = new User("john", "pass");
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Optional<User> result = userService.getUserById(1L);
        assertTrue(result.isPresent());
        assertEquals("john", result.get().getUsername());
    }

    // Test createUser
    @Test
    void testCreateUser() {
    	System.out.println("⚡️ testCreateUser is running...");
        User inputUser = new User("john", "plainpass");
        inputUser.setEmail("john@example.com");
        inputUser.setUsername("john");
        inputUser.setPassword("plainpass");
        inputUser.setRole(1);

        User savedUser = new User("john", PasswordUtil.hashPassword("plainpass"));
        savedUser.setEmail("john@example.com");
        savedUser.setUsername("john");
        savedUser.setRole(1);

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User result = userService.createUser(inputUser);
        assertNotNull(result);
        assertNotEquals("plainpass", result.getPassword());
        verify(userRepository).save(any(User.class));
    }

    // Test updateUser
    @Test
    void testUpdateUser() {
        User existingUser = new User("olduser", "oldpass");
        existingUser.setId(1L);
        existingUser.setEmail("old@example.com");

        User updateDetails = new User("newuser", "newpass");
        updateDetails.setEmail("new@example.com");
        updateDetails.setRole(2);

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User result = userService.updateUser(1L, updateDetails);
        assertNotNull(result);
        assertEquals("newuser", result.getUsername());
        assertEquals("new@example.com", result.getEmail());
        assertNotEquals("newpass", result.getPassword());
        assertTrue(result.getPassword().startsWith("$2"));
    }

    // Test deleteUser
    @Test
    void testDeleteUser() {
        Long userId = 1L;
        User mockUser = new User();
        mockUser.setId(userId);
        mockUser.setAvatar("users/avatar.png"); // hoặc null nếu không cần test delete file
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        userService.deleteUser(userId);
        verify(userRepository).deleteById(userId);
    }

    // Test authenticate (success)
    @Test
    void testAuthenticateSuccess() {
        String email = "user@example.com";
        String password = "password";
        Authentication auth = mock(Authentication.class);

        when(authenticationManager.authenticate(any())).thenReturn(auth);

        boolean result = userService.authenticate(email, password, session);
        assertTrue(result);
        verify(session).setAttribute("user", email);
    }

    // Test authenticate (failure)
    @Test
    void testAuthenticateFailure() {
        String email = "user@example.com";
        String password = "wrongpass";

        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Bad credentials"));

        boolean result = userService.authenticate(email, password, session);
        assertFalse(result);
    }
}
