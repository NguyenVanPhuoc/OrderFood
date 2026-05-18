package com.example.lesson3.controller;

import com.example.lesson3.model.Product;
import com.example.lesson3.service.ProductService;
import com.example.lesson3.utils.FileUploadUtil;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/admin/stores/{storeId}/products")
public class ProductController {

    private static final Logger log = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private ProductService productService;

    @GetMapping
    public String listProducts(
            @PathVariable Long storeId,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir,
            Model model) {

        Page<Product> productPage = productService.findAllWithFilter(storeId, keyword, status, page, size, sortBy, sortDir);

        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("totalItems", productPage.getTotalElements());
        model.addAttribute("size", size);
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("storeId", storeId);
        model.addAttribute("contentPage", "/WEB-INF/views/products/list.jsp");
        model.addAttribute("pageTitle", "Danh sách sản phẩm");
        model.addAttribute("currentPath", storeProductsPath(storeId));
        return "layouts/main";
    }

    @GetMapping("/create")
    public String createForm(@PathVariable Long storeId, Model model) {
        Product product = new Product();
        product.setStoreId(storeId);
        model.addAttribute("product", product);
        return showCreateForm(storeId, model);
    }

    @PostMapping("/save")
    public String saveProduct(
            @PathVariable Long storeId,
            @Valid @ModelAttribute("product") Product product,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            if (product.getId() == null) {
                return showCreateForm(storeId, model);
            } else {
                return showEditForm(storeId, product.getId(), model);
            }
        }

        try {
        	MultipartFile image = product.getImageFile();
        	if (image != null && !image.isEmpty()) {
        	    if (product.getId() != null) {
        	        Optional<Product> existingProductOpt = productService.findById(product.getId());
        	        if (existingProductOpt.isPresent()) {
        	            Product existingProduct = existingProductOpt.get();
        	            if (existingProduct.getImage() != null) {
        	                String oldImageFilename = existingProduct.getImage();
        	                if (oldImageFilename.startsWith("products/")) {
        	                    oldImageFilename = oldImageFilename.substring("products/".length());
        	                }
        	                FileUploadUtil.deleteFile("uploads/products", oldImageFilename);
        	            }
        	        }
        	    }
        	    String filename = FileUploadUtil.saveFile("uploads/products", image);
        	    product.setImage("products/" + filename);
        	} else {
        	    if (product.getId() != null) {
        	        productService.findById(product.getId()).ifPresent(existingProduct -> {
        	            product.setImage(existingProduct.getImage());
        	        });
        	    }
        	}
        } catch (IOException e) {
            log.warn("File upload error (product, storeId={}): {}", storeId, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return redirectToList(storeId);
        }

        product.setStoreId(storeId);
        productService.save(product);
        redirectAttributes.addFlashAttribute("successMessage", "Sản phẩm đã được lưu thành công!");
        return redirectToList(storeId);
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long storeId, @PathVariable Long id, Model model) {
        Product product = productService.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        model.addAttribute("product", product);
        return showEditForm(storeId, id, model);
    }

    @PostMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long storeId, @PathVariable Long id, RedirectAttributes redirectAttributes) {
        productService.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Đã xóa sản phẩm thành công!");
        return redirectToList(storeId);
    }

    @PostMapping("/delete-multiple")
    public String deleteMultipleProducts(
            @PathVariable Long storeId,
            @RequestParam("itemIds") String itemIds,
            RedirectAttributes redirectAttributes) {
        if (itemIds == null || itemIds.isBlank()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Chưa chọn sản phẩm nào để xóa.");
            return redirectToList(storeId);
        }
        try {
            List<Long> ids = Arrays.stream(itemIds.split(","))
                    .filter(s -> !s.isBlank())
                    .map(Long::parseLong)
                    .collect(Collectors.toList());

            productService.deleteMultipleProducts(ids);
            redirectAttributes.addFlashAttribute("successMessage", "Đã xóa các sản phẩm đã chọn thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra khi xóa sản phẩm!");
        }
        return redirectToList(storeId);
    }

    // --- Private helpers ---

    private String storeProductsPath(Long storeId) {
        return "/admin/stores/" + storeId + "/products";
    }

    private String showCreateForm(Long storeId, Model model) {
        model.addAttribute("storeId", storeId);
        model.addAttribute("contentPage", "/WEB-INF/views/products/create.jsp");
        model.addAttribute("pageTitle", "Tạo mới sản phẩm");
        model.addAttribute("currentPath", storeProductsPath(storeId) + "/create");
        return "layouts/main";
    }

    private String showEditForm(Long storeId, Long productId, Model model) {
        model.addAttribute("storeId", storeId);
        model.addAttribute("contentPage", "/WEB-INF/views/products/edit.jsp");
        model.addAttribute("pageTitle", "Chỉnh sửa sản phẩm");
        model.addAttribute("currentPath", storeProductsPath(storeId) + "/edit/" + productId);
        return "layouts/main";
    }

    private String redirectToList(Long storeId) {
        return "redirect:" + storeProductsPath(storeId);
    }
}
