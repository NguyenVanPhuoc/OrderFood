package com.example.lesson3.controller;

import com.example.lesson3.model.User;
import com.example.lesson3.request.UserStoreRequest;
import com.example.lesson3.request.UserUpdateRequest;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.lesson3.service.UserService;
import com.example.lesson3.utils.FileUploadUtil;

@Controller
@RequestMapping("/admin/users")
public class UserController {

	private static final Logger log = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private UserService userService;
	
	@GetMapping
    public String listUsers(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "status", required = false) Integer status,
            Model model) {

        Page<User> userPage = userService.findAllWithFilter(keyword, status, page, size);

        model.addAttribute("users", userPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", userPage.getTotalPages());
        model.addAttribute("totalItems", userPage.getTotalElements());
        model.addAttribute("size", size);
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        model.addAttribute("contentPage", "/WEB-INF/views/users/list.jsp");
	    model.addAttribute("pageTitle", "Danh sách thành viên");
	    model.addAttribute("currentPath", "/admin/users");
	    return "layouts/main";
    }
	
	@GetMapping("/create")
	public String showCreateForm(Model model) {
	    model.addAttribute("user", new User());
	    model.addAttribute("contentPage", "/WEB-INF/views/users/create.jsp");
	    model.addAttribute("pageTitle", "Tạo mới thành viên");
	    model.addAttribute("currentPath", "/admin/users/create");
	    return "layouts/main";
	}
	
	@PostMapping("/create")
	public String storeUser(@Valid @ModelAttribute("user") UserStoreRequest request,
	                        BindingResult bindingResult,
	                        RedirectAttributes redirectAttributes,
	                        Model model) {
	    if (bindingResult.hasErrors()) {
	        model.addAttribute("contentPage", "/WEB-INF/views/users/create.jsp");
	        model.addAttribute("pageTitle", "Tạo mới thành viên");
	        return "layouts/main";
	    }

	    User user = new User();
	    user.setUsername(request.getUsername());
	    user.setName(request.getName());
	    user.setEmail(request.getEmail());
	    user.setPassword(request.getPassword());
	    user.setRole(request.getRole());
	    user.setPhone(request.getPhone());
	    user.setAddress(request.getAddress());
	    user.setStatus(request.getStatus());
	    
	    try {
        	MultipartFile image = request.getImageFile();
        	if (image != null && !image.isEmpty()) {
        	    String filename = FileUploadUtil.saveFile("uploads/users", image);
        	    user.setAvatar("users/" + filename);
        	}
        } catch (IOException e) {
            log.warn("File upload error (create user): {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/users/";
        }

	    userService.createUser(user);

	    redirectAttributes.addFlashAttribute("successMessage", "Tạo mới thành công!");
	    return "redirect:/admin/users";
	}

	
	@GetMapping("/edit/{id}")
	public String editUser(@PathVariable Long id, Model model) {
	    Optional<User> userOptional = userService.getUserById(id);
	    if (userOptional.isPresent()) {
	        model.addAttribute("user", userOptional.get());
	        model.addAttribute("contentPage", "/WEB-INF/views/users/edit.jsp");
	        model.addAttribute("pageTitle", "Chỉnh sửa thành viên");
	        model.addAttribute("currentPath", "/admin/users");
	        return "layouts/main";
	    } else {
	        return "redirect:/admin/users";
	    }
	}
	

	@PostMapping("/edit/{id}")
	public String updateUser(@PathVariable Long id,
							@Valid @ModelAttribute("user") UserUpdateRequest request,
	                         BindingResult result,
	                         RedirectAttributes redirectAttributes, Model model) {
	    if (result.hasErrors()) {
	    	model.addAttribute("user", request);
	    	model.addAttribute("contentPage", "/WEB-INF/views/users/edit.jsp");
	        model.addAttribute("pageTitle", "Chỉnh sửa thành viên");
	        model.addAttribute("currentPath", "/admin/users");
	        return "layouts/main";
	    }

	    User user = new User();
	    user.setId(id);
	    user.setUsername(request.getUsername());
	    user.setEmail(request.getEmail());
	    user.setName(request.getName());
	    user.setRole(request.getRole());
	    user.setPhone(request.getPhone());
	    user.setAddress(request.getAddress());
	    user.setStatus(request.getStatus());

	    try {
        	MultipartFile image = request.getImageFile();
        	if (image != null && !image.isEmpty()) {
        	    if (request.getId() != null) {
        	        Optional<User> existingUserOpt = userService.getUserById(request.getId());
        	        if (existingUserOpt.isPresent()) {
        	        	User existingUser = existingUserOpt.get();
        	            if (existingUser.getAvatar() != null) {
        	                String oldImageFilename = existingUser.getAvatar();
        	                if (oldImageFilename.startsWith("users/")) {
        	                    oldImageFilename = oldImageFilename.substring("users/".length());
        	                }
        	                FileUploadUtil.deleteFile("uploads/users", oldImageFilename);
        	            }
        	        }
        	    }
        	    String filename = FileUploadUtil.saveFile("uploads/users", image);
        	    user.setAvatar("users/" + filename);
        	} else {
        	    if (request.getId() != null) {
        	    	userService.getUserById(request.getId()).ifPresent(existingUser -> {
        	    		user.setAvatar(existingUser.getAvatar());
        	        });
        	    }
        	}
        } catch (IOException e) {
            log.warn("File upload error (edit user id={}): {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/users/";
        }

	    userService.updateUser(id, user);

	    redirectAttributes.addFlashAttribute("successMessage", "Chỉnh sửa thành công!");
	    return "redirect:/admin/users";
	}



    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        userService.deleteUser(id);
        redirectAttributes.addFlashAttribute("successMessage", "Đã xóa thành công!");
        return "redirect:/admin/users";
    }

    @PostMapping("/delete-multiple")
    public String deleteMultipleUsers(
            @RequestParam("itemIds") String itemIds,
            RedirectAttributes redirectAttributes) {
        if (itemIds == null || itemIds.isBlank()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Chưa chọn thành viên nào để xóa.");
            return "redirect:/admin/users";
        }
        try {
            List<Long> ids = Arrays.stream(itemIds.split(","))
                    .filter(s -> !s.isBlank())
                    .map(Long::parseLong)
                    .collect(Collectors.toList());
            
            userService.deleteMultipleUsers(ids);
            redirectAttributes.addFlashAttribute("successMessage", "Đã xóa các thành viên đã chọn thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra khi xóa thành viên!");
        }
        return "redirect:/admin/users";
    }
}
