package com.example.lesson3.repository;

import com.example.lesson3.model.Product;
import com.example.lesson3.model.Store;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Test
    void testQueries() {
        Store store = new Store();
        store.setName("Coffee House");
        store.setAddress("123 Test St");
        store.setStatus(1);
        store.setSlug("x2");
        store = storeRepository.save(store);

        Product a = new Product(); a.setName("Black Tea"); a.setStatus(1); a.setStore(store); a.setPrice(BigDecimal.valueOf(10.0));
        Product b = new Product(); b.setName("Green Tea"); b.setStatus(0); b.setStore(store); b.setPrice(BigDecimal.valueOf(12.5));
        productRepository.save(a); productRepository.save(b);

        Page<Product> all = productRepository.findByStore_Id(store.getId(), PageRequest.of(0, 10));
        assertTrue(all.getTotalElements() >= 2);

        Page<Product> byStatus = productRepository.findByStore_IdAndStatus(store.getId(), 1, PageRequest.of(0, 10));
        assertTrue(byStatus.getContent().stream().allMatch(p -> p.getStatus() == 1));

        Page<Product> byName = productRepository.findByStore_IdAndNameContainingIgnoreCase(store.getId(), "tea", PageRequest.of(0, 10));
        assertFalse(byName.isEmpty());

        Page<Product> byNameAndStatus = productRepository.findByStore_IdAndNameContainingIgnoreCaseAndStatus(store.getId(), "tea", 1, PageRequest.of(0, 10));
        assertTrue(byNameAndStatus.getContent().stream().allMatch(p -> p.getStatus() == 1));
    }
}
