package com.example.lesson3.controller;

import com.example.lesson3.model.Store;
import com.example.lesson3.service.StoreService;
import com.example.lesson3.utils.FileUploadUtil;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/stores")
public class StoreController {

    @Autowired
    private StoreService storeService;
    
    @GetMapping
    public String listStores(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "status", required = false) Integer status,
            Model model) {

        Page<Store> storePage = storeService.findAllWithFilter(keyword, status, page, size);

        model.addAttribute("stores", storePage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", storePage.getTotalPages());
        model.addAttribute("totalItems", storePage.getTotalElements());
        model.addAttribute("size", size);
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        model.addAttribute("contentPage", "/WEB-INF/views/stores/list.jsp");
	    model.addAttribute("pageTitle", "Danh sách cửa hàng");
	    model.addAttribute("currentPath", "/admin/stores");
	    return "layouts/main";
    }
    
    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("store", new Store());
        model.addAttribute("contentPage", "/WEB-INF/views/stores/create.jsp");
	    model.addAttribute("pageTitle", "Tạo mới cửa hàng");
	    model.addAttribute("currentPath", "/admin/stores/create");
	    return "layouts/main";
    }
    
    @PostMapping("/save")
    public String saveStore(
        @Valid @ModelAttribute("store") Store store,
        BindingResult bindingResult,
        RedirectAttributes redirectAttributes,
        Model model) {
    	
    	if (store.getOrderStartTime() != null && store.getOrderEndTime() != null) {
    	    if (store.getOrderStartTime().compareTo(store.getOrderEndTime()) >= 0) {
    	        bindingResult.rejectValue("orderStartTime", "error.store", "Giờ bắt đầu phải nhỏ hơn giờ kết thúc");
    	    }
    	}

        if (bindingResult.hasErrors()) {
            if (store.getId() == null) {
                model.addAttribute("contentPage", "/WEB-INF/views/stores/create.jsp");
                model.addAttribute("currentPath", "/admin/stores/create");
                model.addAttribute("pageTitle", "Tạo mới cửa hàng");
            } else {
                model.addAttribute("contentPage", "/WEB-INF/views/stores/edit.jsp");
                model.addAttribute("currentPath", "/admin/stores/edit/" + store.getId());
                model.addAttribute("pageTitle", "Chỉnh sửa cửa hàng");
            }
            return "layouts/main";
        }
        
        try {
        	MultipartFile image = store.getImageFile();
        	if (image != null && !image.isEmpty()) {
        	    if (store.getId() != null) {
        	        Optional<Store> existingProductOpt = storeService.findById(store.getId());
        	        if (existingProductOpt.isPresent()) {
        	        	Store existingProduct = existingProductOpt.get();
        	            if (existingProduct.getImage() != null) {
        	                String oldImageFilename = existingProduct.getImage();
        	                if (oldImageFilename.startsWith("stores/")) {
        	                    oldImageFilename = oldImageFilename.substring("stores/".length());
        	                }
        	                FileUploadUtil.deleteFile("uploads/stores", oldImageFilename);
        	            }
        	        }
        	    }
        	    String filename = FileUploadUtil.saveFile("uploads/stores", image);
        	    store.setImage("stores/" + filename);
        	} else {
        	    if (store.getId() != null) {
        	    	storeService.findById(store.getId()).ifPresent(existingProduct -> {
        	    		store.setImage(existingProduct.getImage());
        	        });
        	    }
        	}
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi khi xử lý ảnh.");
            return "redirect:/admin/stores/";
        }

        storeService.saveStore(store);
        redirectAttributes.addFlashAttribute("successMessage", "Cửa hàng đã được lưu thành công!");
        return "redirect:/admin/stores";
    }

    @GetMapping("/edit/{id}")
    public String editStore(@PathVariable Long id, Model model) {
    	Store store = storeService.findById(id).orElseThrow(() -> new RuntimeException("Store not found"));
        model.addAttribute("store", store);
        model.addAttribute("pageTitle", "Chỉnh sửa cửa hàng");
        model.addAttribute("contentPage", "/WEB-INF/views/stores/edit.jsp");
        model.addAttribute("currentPath", "/admin/stores/edit");
	    return "layouts/main";
    }
    
    @PostMapping("/delete/{id}")
    public String deleteStore(@PathVariable(value = "id") Long id, RedirectAttributes redirectAttributes) {
    	storeService.deleteStore(id);
        redirectAttributes.addFlashAttribute("successMessage", "Đã xóa cửa hàng thành công!");
        return "redirect:/admin/stores";
    }

    @PostMapping("/delete-multiple")
    public String deleteMultipleStores(@RequestParam("itemIds") String itemIds, RedirectAttributes redirectAttributes) {
        try {
            List<Long> ids = Arrays.stream(itemIds.split(","))
                    .map(Long::parseLong)
                    .collect(Collectors.toList());
            
            storeService.deleteMultipleStores(ids);
            redirectAttributes.addFlashAttribute("successMessage", "Đã xóa các cửa hàng đã chọn thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra khi xóa cửa hàng!");
        }
        return "redirect:/admin/stores";
    }
}
