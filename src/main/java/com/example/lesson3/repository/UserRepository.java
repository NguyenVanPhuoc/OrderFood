package com.example.lesson3.repository;

import com.example.lesson3.model.User;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByEmail(String email);
	boolean existsByEmail(String email);
	Page<User> findByNameContainingIgnoreCase(String keyword, Pageable pageable);
    Page<User> findByNameContainingIgnoreCaseAndStatus(String keyword, Integer status, Pageable pageable);
    Page<User> findByStatus(Integer status, Pageable pageable);
}
