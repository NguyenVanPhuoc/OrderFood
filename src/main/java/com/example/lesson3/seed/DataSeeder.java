package com.example.lesson3.seed;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.lesson3.model.User;
import com.example.lesson3.repository.UserRepository;
import com.example.lesson3.utils.PasswordUtil;

/**
 * Seed user để đăng nhập khi chạy lần đầu.
 * Chỉ chạy khi database chưa có user nào.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;

    public DataSeeder(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return;
        }

        // Admin
        User admin = new User();
        admin.setName("Quản trị viên");
        admin.setEmail("admin@bau.com");
        admin.setUsername("admin");
        admin.setPassword(PasswordUtil.hashPassword("123456"));
        admin.setRole(1);
        admin.setPhone("0901234567");
        admin.setAddress("Hà Nội");
        admin.setStatus(1);
        userRepository.save(admin);

        // User
        User user1 = new User();
        user1.setName("Nguyễn Văn A");
        user1.setEmail("user@test.com");
        user1.setUsername("user");
        user1.setPassword(PasswordUtil.hashPassword("123456"));
        user1.setRole(2);
        user1.setPhone("0987654321");
        user1.setAddress("TP.HCM");
        user1.setStatus(1);
        userRepository.save(user1);
    }
}
