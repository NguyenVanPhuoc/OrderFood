package com.example.lesson3.service;

import com.example.lesson3.model.Product;
import com.example.lesson3.repository.ProductRepository;
import com.example.lesson3.utils.FileUploadUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

	private static final Logger log = LoggerFactory.getLogger(ProductService.class);

	@Autowired
	private ProductRepository productRepository;

	private static final List<String> ALLOWED_SORT_FIELDS = Arrays.asList("id", "name", "price");

	public Page<Product> findAllWithFilter(Long storeId, String keyword, Integer status, int page, int size, String sortBy, String sortDir) {
		if (storeId == null) throw new IllegalArgumentException("storeId không được null");
		String safeSortBy = ALLOWED_SORT_FIELDS.contains(sortBy) ? sortBy : "id";
		Sort.Direction direction = "desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC;
		Sort sort = Sort.by(direction, safeSortBy);
		int safePage = Math.max(1, page);
		int safeSize = Math.min(Math.max(1, size), 100);
		Pageable pageable = PageRequest.of(safePage - 1, safeSize, sort);

		if (keyword != null && !keyword.isEmpty() && status != null) {
			return productRepository.findByStore_IdAndNameContainingIgnoreCaseAndStatus(storeId, keyword, status, pageable);
		} else if (keyword != null && !keyword.isEmpty()) {
			return productRepository.findByStore_IdAndNameContainingIgnoreCase(storeId, keyword, pageable);
		} else if (status != null) {
			return productRepository.findByStore_IdAndStatus(storeId, status, pageable);
		} else {
			return productRepository.findByStore_Id(storeId, pageable);
		}
	}

	public Product save(Product product) {
		if (product.getId() != null) {
			productRepository.findById(product.getId()).ifPresent(old -> product.setCreatedAt(old.getCreatedAt()));
		}
		return productRepository.save(product);
	}

	public Optional<Product> findById(Long id) {
		return productRepository.findById(id);
	}

	public List<Product> findByStoreId(Long storeId) {
		return productRepository.findByStore_IdOrderByStatusAscCreatedAtAsc(storeId);
	}

	public void deleteById(Long id) {
		productRepository.findById(id).ifPresent(product -> {
			deleteProductImage(product);
			productRepository.deleteById(id);
		});
	}

	public List<Product> findByIds(List<Long> ids) {
		return productRepository.findAllById(ids);
	}

	@Transactional
	public void deleteMultipleProducts(List<Long> ids) {
		if (ids == null || ids.isEmpty()) return;
		List<Product> products = productRepository.findAllById(ids);
		productRepository.deleteAll(products);
		for (Product product : products) {
			deleteProductImage(product);
		}
		log.info("Đã xóa {} sản phẩm", products.size());
	}

	private void deleteProductImage(Product product) {
		if (product.getImage() != null) {
			String imagePath = product.getImage();
			if (imagePath.startsWith("products/")) {
				imagePath = imagePath.substring("products/".length());
			}
			try {
				FileUploadUtil.deleteFile("uploads/products", imagePath);
			} catch (IOException e) {
				log.warn("Không thể xóa file ảnh product {}: {}", imagePath, e.getMessage());
			}
		}
	}
}
