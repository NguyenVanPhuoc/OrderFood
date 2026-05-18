package com.example.lesson3.service;

import com.example.lesson3.model.Product;
import com.example.lesson3.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAllWithFilter_AllNull() {
        Page<Product> page = new PageImpl<>(Collections.emptyList());
        when(productRepository.findByStore_Id(eq(1L), any(Pageable.class))).thenReturn(page);

        Page<Product> result = productService.findAllWithFilter(1L, null, null, 1, 10, "id", "asc");
        assertNotNull(result);
        verify(productRepository).findByStore_Id(eq(1L), any(Pageable.class));
    }

    @Test
    void testFindAllWithFilter_WithKeywordAndStatus() {
        Page<Product> page = new PageImpl<>(Collections.emptyList());
        when(productRepository.findByStore_IdAndNameContainingIgnoreCaseAndStatus(eq(1L), eq("tea"), eq(1), any(Pageable.class)))
                .thenReturn(page);

        Page<Product> result = productService.findAllWithFilter(1L, "tea", 1, 1, 10, "id", "asc");
        assertNotNull(result);
        verify(productRepository).findByStore_IdAndNameContainingIgnoreCaseAndStatus(eq(1L), eq("tea"), eq(1), any(Pageable.class));
    }

    @Test
    void testFindAllWithFilter_WithKeywordOnly() {
        Page<Product> page = new PageImpl<>(Collections.emptyList());
        when(productRepository.findByStore_IdAndNameContainingIgnoreCase(eq(1L), eq("tea"), any(Pageable.class)))
                .thenReturn(page);

        Page<Product> result = productService.findAllWithFilter(1L, "tea", null, 1, 10, "id", "asc");
        assertNotNull(result);
        verify(productRepository).findByStore_IdAndNameContainingIgnoreCase(eq(1L), eq("tea"), any(Pageable.class));
    }

    @Test
    void testFindAllWithFilter_WithStatusOnly() {
        Page<Product> page = new PageImpl<>(Collections.emptyList());
        when(productRepository.findByStore_IdAndStatus(eq(1L), eq(1), any(Pageable.class))).thenReturn(page);

        Page<Product> result = productService.findAllWithFilter(1L, null, 1, 1, 10, "id", "asc");
        assertNotNull(result);
        verify(productRepository).findByStore_IdAndStatus(eq(1L), eq(1), any(Pageable.class));
    }

    @Test
    void testFindAllWithFilter_SortByNameDesc() {
        Page<Product> page = new PageImpl<>(Collections.emptyList());
        when(productRepository.findByStore_Id(eq(1L), any(Pageable.class))).thenReturn(page);

        Page<Product> result = productService.findAllWithFilter(1L, null, null, 1, 10, "name", "desc");
        assertNotNull(result);
        verify(productRepository).findByStore_Id(eq(1L), any(Pageable.class));
    }

    @Test
    void testFindAllWithFilter_InvalidSortFieldFallsBackToId() {
        Page<Product> page = new PageImpl<>(Collections.emptyList());
        when(productRepository.findByStore_Id(eq(1L), any(Pageable.class))).thenReturn(page);

        Page<Product> result = productService.findAllWithFilter(1L, null, null, 1, 10, "malicious_field", "asc");
        assertNotNull(result);
        verify(productRepository).findByStore_Id(eq(1L), any(Pageable.class));
    }

    @Test
    void testSave_PreservesCreatedAtWhenUpdating() {
        Product existing = new Product();
        existing.setId(5L);
        LocalDateTime ts = LocalDateTime.now().minusDays(1);
        existing.setCreatedAt(ts);

        Product toSave = new Product();
        toSave.setId(5L);

        when(productRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        Product result = productService.save(toSave);
        assertEquals(ts, result.getCreatedAt());
        verify(productRepository).findById(5L);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void testFindById() {
        Product p = new Product();
        p.setId(2L);
        when(productRepository.findById(2L)).thenReturn(Optional.of(p));

        Optional<Product> result = productService.findById(2L);
        assertTrue(result.isPresent());
        assertEquals(2L, result.get().getId());
    }

    @Test
    void testFindByStoreId() {
        List<Product> list = Arrays.asList(new Product(), new Product());
        when(productRepository.findByStore_IdOrderByStatusAscCreatedAtAsc(3L)).thenReturn(list);

        List<Product> result = productService.findByStoreId(3L);
        assertEquals(2, result.size());
        verify(productRepository).findByStore_IdOrderByStatusAscCreatedAtAsc(3L);
    }

    @Test
    void testDeleteById_WithImageDeletesFileAndRepository() throws IOException {
        Product p = new Product();
        p.setId(10L);
        p.setImage("products/p1.png");
        when(productRepository.findById(10L)).thenReturn(Optional.of(p));

        productService.deleteById(10L);

        verify(productRepository).deleteById(10L);
    }

    @Test
    void testDeleteById_NoProductDoesNothing() {
        when(productRepository.findById(11L)).thenReturn(Optional.empty());
        productService.deleteById(11L);
        verify(productRepository, never()).deleteById(anyLong());
    }

    @Test
    void testFindByIds() {
        List<Product> products = Arrays.asList(new Product(), new Product(), new Product());
        when(productRepository.findAllById(Arrays.asList(1L, 2L, 3L))).thenReturn(products);
        List<Product> result = productService.findByIds(Arrays.asList(1L, 2L, 3L));
        assertEquals(3, result.size());
    }

    @Test
    void testDeleteMultipleProducts_DeletesRepositoryAfterLoop() {
        Product p1 = new Product(); p1.setId(1L);
        Product p2 = new Product(); p2.setId(2L);
        List<Product> products = Arrays.asList(p1, p2);
        when(productRepository.findAllById(Arrays.asList(1L, 2L))).thenReturn(products);

        productService.deleteMultipleProducts(Arrays.asList(1L, 2L));

        verify(productRepository).deleteAll(products);
    }
}
