package com.example.lesson3.service;

import com.example.lesson3.model.User;
import com.example.lesson3.repository.UserRepository;
import com.example.lesson3.utils.PasswordUtil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpSession;

import java.util.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

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

    // Test isEmailTaken — email đã tồn tại
    @Test
    void testIsEmailTaken_WhenEmailExists_ReturnsTrue() {
        when(userRepository.existsByEmail("taken@example.com")).thenReturn(true);
        assertTrue(userService.isEmailTaken("taken@example.com"));
        verify(userRepository).existsByEmail("taken@example.com");
    }

    // Test isEmailTaken — email chưa tồn tại
    @Test
    void testIsEmailTaken_WhenEmailNotExists_ReturnsFalse() {
        when(userRepository.existsByEmail("free@example.com")).thenReturn(false);
        assertFalse(userService.isEmailTaken("free@example.com"));
    }

    // Test saveUser
    @Test
    void testSaveUser_DelegatesToRepository() {
        User user = new User("alice", "pass");
        user.setId(5L);
        when(userRepository.save(user)).thenReturn(user);

        User result = userService.saveUser(user);

        assertNotNull(result);
        assertEquals(5L, result.getId());
        verify(userRepository).save(user);
    }

    // Test findByEmail — tìm thấy
    @Test
    void testFindByEmail_UserFound_ReturnsUser() {
        User user = new User("bob", "pass");
        user.setEmail("bob@example.com");
        when(userRepository.findByEmail("bob@example.com")).thenReturn(Optional.of(user));

        User result = userService.findByEmail("bob@example.com");

        assertNotNull(result);
        assertEquals("bob@example.com", result.getEmail());
        verify(userRepository).findByEmail("bob@example.com");
    }

    // Test findByEmail — không tìm thấy → ném ngoại lệ
    @Test
    void testFindByEmail_UserNotFound_ThrowsRuntimeException() {
        when(userRepository.findByEmail("nobody@example.com")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.findByEmail("nobody@example.com"));
    }

    // Test deleteMultipleUsers
    @Test
    void testDeleteMultipleUsers_DeletesAllAndAvatars() {
        User u1 = new User(); u1.setId(1L); u1.setAvatar(null);
        User u2 = new User(); u2.setId(2L); u2.setAvatar(null);
        List<Long> ids = Arrays.asList(1L, 2L);
        when(userRepository.findAllById(ids)).thenReturn(Arrays.asList(u1, u2));

        userService.deleteMultipleUsers(ids);

        verify(userRepository).findAllById(ids);
        verify(userRepository).deleteAll(Arrays.asList(u1, u2));
    }

    // Test findAllWithFilter — keyword + status
    @Test
    void testFindAllWithFilter_WithKeywordAndStatus() {
        Page<User> mockPage = new PageImpl<>(List.of(new User()));
        when(userRepository.findByNameContainingIgnoreCaseAndStatus(eq("alice"), eq(1), any(Pageable.class)))
                .thenReturn(mockPage);

        Page<User> result = userService.findAllWithFilter("alice", 1, 1, 10);

        assertEquals(1, result.getTotalElements());
        verify(userRepository).findByNameContainingIgnoreCaseAndStatus(eq("alice"), eq(1), any(Pageable.class));
    }

    // Test findAllWithFilter — chỉ keyword
    @Test
    void testFindAllWithFilter_WithKeywordOnly() {
        Page<User> mockPage = new PageImpl<>(List.of(new User(), new User()));
        when(userRepository.findByNameContainingIgnoreCase(eq("john"), any(Pageable.class)))
                .thenReturn(mockPage);

        Page<User> result = userService.findAllWithFilter("john", null, 1, 10);

        assertEquals(2, result.getTotalElements());
        verify(userRepository).findByNameContainingIgnoreCase(eq("john"), any(Pageable.class));
    }

    // Test findAllWithFilter — chỉ status
    @Test
    void testFindAllWithFilter_WithStatusOnly() {
        Page<User> mockPage = new PageImpl<>(List.of(new User()));
        when(userRepository.findByStatus(eq(0), any(Pageable.class))).thenReturn(mockPage);

        Page<User> result = userService.findAllWithFilter(null, 0, 1, 10);

        assertEquals(1, result.getTotalElements());
        verify(userRepository).findByStatus(eq(0), any(Pageable.class));
    }

    // Test findAllWithFilter — không có filter
    @Test
    void testFindAllWithFilter_NoFilter_ReturnsAll() {
        Page<User> mockPage = new PageImpl<>(List.of(new User(), new User(), new User()));
        when(userRepository.findAll(any(Pageable.class))).thenReturn(mockPage);

        Page<User> result = userService.findAllWithFilter(null, null, 1, 10);

        assertEquals(3, result.getTotalElements());
        verify(userRepository).findAll(any(Pageable.class));
    }
}
