package com.example.lesson3.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.lesson3.model.User;
import com.example.lesson3.repository.UserRepository;
import com.example.lesson3.utils.FileUploadUtil;
import com.example.lesson3.utils.PasswordUtil;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

@Service
public class UserService {

	private static final Logger log = LoggerFactory.getLogger(UserService.class);

	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private UserRepository userRepository;

	public boolean authenticate(String email, String password, HttpSession session) {
		try {
			Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(email, password));
			SecurityContextHolder.getContext().setAuthentication(authentication);
			session.setAttribute("user", email);
			return true;
		} catch (Exception e) {
			log.warn("Đăng nhập thất bại cho email: {}", email);
			return false;
		}
	}

	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	public Page<User> findAllWithFilter(String keyword, Integer status, int page, int size) {
		Sort sort = Sort.by("id").descending();
		Pageable pageable = PageRequest.of(page - 1, size, sort);

		if (keyword != null && !keyword.isEmpty() && status != null) {
			return userRepository.findByNameContainingIgnoreCaseAndStatus(keyword, status, pageable);
		} else if (keyword != null && !keyword.isEmpty()) {
			return userRepository.findByNameContainingIgnoreCase(keyword, pageable);
		} else if (status != null) {
			return userRepository.findByStatus(status, pageable);
		} else {
			return userRepository.findAll(pageable);
		}
	}

	public Optional<User> getUserById(Long id) {
		return userRepository.findById(id);
	}

	public User createUser(User user) {
		if (user.getPassword() != null && !user.getPassword().isEmpty()) {
			user.setPassword(PasswordUtil.hashPassword(user.getPassword()));
		}
		return userRepository.save(user);
	}

	public User saveUser(User user) {
		return userRepository.save(user);
	}

	public User updateUser(Long id, User userDetails) {
		return userRepository.findById(id).map(user -> {
			user.setUsername(userDetails.getUsername());
			user.setName(userDetails.getName());
			user.setEmail(userDetails.getEmail());
			user.setRole(userDetails.getRole());
			user.setPhone(userDetails.getPhone());
			user.setAddress(userDetails.getAddress());
			user.setAvatar(userDetails.getAvatar());
			if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
				user.setPassword(PasswordUtil.hashPassword(userDetails.getPassword()));
			}
			return userRepository.save(user);
		}).orElse(null);
	}

	public void deleteUser(Long id) {
		userRepository.findById(id).ifPresent(user -> {
			deleteUserAvatar(user);
			userRepository.deleteById(id);
		});
	}

	public User findByEmail(String email) {
		return userRepository.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("User not found"));
	}

	public void deleteMultipleUsers(List<Long> ids) {
		List<User> users = userRepository.findAllById(ids);
		for (User user : users) {
			deleteUserAvatar(user);
		}
		userRepository.deleteAll(users);
		log.info("Đã xóa {} người dùng", users.size());
	}

	private void deleteUserAvatar(User user) {
		if (user.getAvatar() != null) {
			String imagePath = user.getAvatar();
			if (imagePath.startsWith("users/")) {
				imagePath = imagePath.substring("users/".length());
			}
			try {
				FileUploadUtil.deleteFile("uploads/users", imagePath);
			} catch (IOException e) {
				log.warn("Không thể xóa avatar user {}: {}", user.getId(), e.getMessage());
			}
		}
	}
}
