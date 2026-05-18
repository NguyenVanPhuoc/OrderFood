package com.example.lesson3.request;

import javax.persistence.Transient;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateRequest {
	private Long id;
	 
	@NotBlank(message = "Username không được để trống!")
	@Size(max = 50, message = "Username không được vượt quá 50 ký tự!")
	private String username;

	@NotBlank(message = "Họ và tên không được để trống!")
	@Size(max = 50, message = "Họ và tên không được vượt quá 50 ký tự!")
	private String name;

	@NotBlank(message = "Email không được để trống!")
	@Email(message = "Email không hợp lệ")
	@Size(max = 50, message = "Email không được vượt quá 50 ký tự!")
	private String email;
    
    private int role;

    private int status;

    @Pattern(regexp = "^$|^[0-9]{10,12}$", message = "Số điện thoại phải từ 10 đến 12 chữ số!")
    private String phone;

    @Size(max = 255, message = "Địa chỉ không được vượt quá 255 ký tự!")
    private String address;
    
    @Transient
    private MultipartFile imageFile;
    
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

	public int getRole() {
		return role;
	}

	public void setRole(int role) {
		this.role = role;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
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

	public MultipartFile getImageFile() {
		return imageFile;
	}

	public void setImageFile(MultipartFile imageFile) {
		this.imageFile = imageFile;
	}
	
}
