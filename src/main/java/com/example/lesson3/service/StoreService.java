package com.example.lesson3.service;

import com.example.lesson3.constants.StoreStatus;
import com.example.lesson3.model.Store;
import com.example.lesson3.repository.StoreRepository;
import com.example.lesson3.utils.FileUploadUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class StoreService {

    private static final Logger log = LoggerFactory.getLogger(StoreService.class);

    @Autowired
    private StoreRepository storeRepository;

    public List<Store> getAllStores() {
        return storeRepository.findByStatus(StoreStatus.ACTIVE);
    }

    public Page<Store> findAllWithFilter(String keyword, Integer status, int page, int size) {
        Sort sort = Sort.by("id").descending();
        Pageable pageable = PageRequest.of(page - 1, size, sort);

        if (keyword != null && !keyword.isEmpty() && status != null) {
            return storeRepository.findByNameContainingIgnoreCaseAndStatus(keyword, status, pageable);
        } else if (keyword != null && !keyword.isEmpty()) {
            return storeRepository.findByNameContainingIgnoreCase(keyword, pageable);
        } else if (status != null) {
            return storeRepository.findByStatus(status, pageable);
        } else {
            return storeRepository.findAll(pageable);
        }
    }

    public Optional<Store> findById(Long id) {
        return storeRepository.findById(id);
    }

    public Optional<Store> findBySlug(String slug) {
        return storeRepository.findBySlug(slug);
    }

    public Store saveStore(Store store) {
        if (store.getId() != null) {
            if (storeRepository.existsBySlugAndIdNot(store.getSlug(), store.getId())) {
                throw new IllegalArgumentException("Slug đã tồn tại, vui lòng chọn slug khác");
            }
        } else {
            if (storeRepository.existsBySlug(store.getSlug())) {
                throw new IllegalArgumentException("Slug đã tồn tại, vui lòng chọn slug khác");
            }
        }
        return storeRepository.save(store);
    }

    public void deleteStore(Long id) {
        Optional<Store> storeOpt = storeRepository.findById(id);
        if (storeOpt.isPresent()) {
            Store store = storeOpt.get();
            if (store.getImage() != null) {
                String imagePath = store.getImage();
                if (imagePath.startsWith("stores/")) {
                    imagePath = imagePath.substring("stores/".length());
                }
                try {
                    FileUploadUtil.deleteFile("uploads/stores", imagePath);
                } catch (IOException e) {
                    log.warn("Không thể xóa file ảnh store {}: {}", imagePath, e.getMessage());
                }
            }
            storeRepository.deleteById(id);
        }
    }

    public void deleteMultipleStores(List<Long> ids) {
        List<Store> stores = storeRepository.findAllById(ids);
        for (Store store : stores) {
            if (store.getImage() != null) {
                String imagePath = store.getImage();
                if (imagePath.startsWith("stores/")) {
                    imagePath = imagePath.substring("stores/".length());
                }
                try {
                    FileUploadUtil.deleteFile("uploads/stores", imagePath);
                } catch (IOException e) {
                    log.warn("Không thể xóa file ảnh store {}: {}", imagePath, e.getMessage());
                }
            }
        }
        storeRepository.deleteAll(stores);
        log.info("Đã xóa {} cửa hàng", stores.size());
    }

}
