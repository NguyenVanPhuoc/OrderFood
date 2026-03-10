package com.example.lesson3.service;

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
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
    private UserRepository userRepository; 
	
	public boolean authenticate(String email, String password, HttpSession session) {
		try {
			Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(email, password)
					);
			
			SecurityContextHolder.getContext().setAuthentication(authentication);
			session.setAttribute("user", email);
			return true;
		} catch (Exception e) {
			System.out.println("Authentication error: " + e.getMessage());
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
	
	// Lấy user theo ID
	public Optional<User> getUserById(Long id) {
		return userRepository.findById(id);
	}
	
	// Tạo user mới
	public User createUser(User user) {
		if (user.getPassword() != null && !user.getPassword().isEmpty()) {
			String hashedPassword = PasswordUtil.hashPassword(user.getPassword());
			user.setPassword(hashedPassword);
		}
		return userRepository.save(user);
	}
	
	// Lưu user
	public User saveUser(User user) {
		return userRepository.save(user);
	}
	
	// Cập nhật user (chỉ dùng cho admin)
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
	
	// Xóa user
	public void deleteUser(Long id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
        	User user = userOpt.get();
            if (user.getAvatar() != null) {
                String imagePath = user.getAvatar();
                if (imagePath.startsWith("users/")) {
                    imagePath = imagePath.substring("users/".length());
                }
                try {
                    FileUploadUtil.deleteFile("uploads/users", imagePath);
                } catch (IOException e) {
                    System.err.println("Không thể xóa file ảnh: " + imagePath);
                    e.printStackTrace();
                }
            }
            userRepository.deleteById(id);
        }
    }
	
	public User findByEmail(String email) {
	    return userRepository.findByEmail(email)
	        .orElseThrow(() -> new RuntimeException("User not found"));
	}

    public void deleteMultipleUsers(List<Long> ids) {
        List<User> users = userRepository.findAllById(ids);
        System.out.println("Số lượng người dùng cần xóa: " + users.size());
        for (User user : users) {
            System.out.println("Đang xử lý người dùng ID: " + user.getId());
            if (user.getAvatar() != null) {
                String imagePath = user.getAvatar();
                System.out.println("Đường dẫn ảnh gốc: " + imagePath);
                if (imagePath.startsWith("users/")) {
                    imagePath = imagePath.substring("users/".length());
                }
                System.out.println("Đường dẫn ảnh sau khi xử lý: " + imagePath);
                try {
                    FileUploadUtil.deleteFile("uploads/users", imagePath);
                    System.out.println("Đã xóa file ảnh thành công: " + imagePath);
                } catch (IOException e) {
                    System.err.println("Không thể xóa file ảnh: " + imagePath);
                    e.printStackTrace();
                }
            } else {
                System.out.println("Người dùng không có ảnh");
            }
        }
        userRepository.deleteAll(users);
        System.out.println("Đã xóa " + users.size() + " người dùng khỏi database");
    }
}