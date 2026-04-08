package com.example.lesson3.model;

import java.time.LocalDateTime;

import javax.persistence.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
public class User {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@Column(nullable = true)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(unique = true, nullable = false)
    private String username;
    
    @Column(nullable = false)
    private String password;
    
    @Column(nullable = false)
    private int role;
    
    @Column(nullable = true)
    private String phone;

    @Column(nullable = true, length = 255)
    private String address;
    
    @Transient
    private MultipartFile imageFile;

	private String avatar;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime updatedAt;
    
    @Column(name = "status", columnDefinition = "int default 1")
    private int status;

    public boolean isActive() {
        return status == 1;
    }
    
    @PrePersist
    public void prePersist() {
    	if (role == 0) {
            this.role = 1;
        }
    }

    public User() {}

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public Long getId() { 
    	return id; 
    }
    public void setId(Long id) { 
    	this.id = id; 
    }

    public String getUsername() { 
    	return username; 
    }
    
    public void setUsername(String username) { 
    	this.username = username; 
    }

    public String getPassword() { 
    	return password; 
    }
    
    public void setPassword(String password) {
    	this.password = password; 
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getStatusName() {
	    switch (this.status) {
	        case 1:
	            return "Kích hoạt";
	        case 2:
	            return "Không kích hoạt";
	        default:
	            return "UNKNOWN";
	    }
	}
	
	public String getRoleName() {
	    switch (this.role) {
	        case 1:
	            return "Admin";
	        case 2:
	            return "User";
	        default:
	            return "UNKNOWN";
	    }
	}

	public int getRole() {
		return role;
	}

	public void setRole(int role) {
		this.role = role;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	public MultipartFile getImageFile() {
		return imageFile;
	}

	public void setImageFile(MultipartFile imageFile) {
		this.imageFile = imageFile;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	
}

