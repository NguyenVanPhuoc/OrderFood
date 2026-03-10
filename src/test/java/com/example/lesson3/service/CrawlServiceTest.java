package com.example.lesson3.service;

import com.example.lesson3.repository.ProductRepository;
import com.example.lesson3.repository.StoreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

/**
 * Test cho CrawlService.
 * Chỉ test logic không phụ thuộc network (store not found).
 */
class CrawlServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private StoreRepository storeRepository;

    @InjectMocks
    private CrawlService crawlService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCrawlGrabFoodMenu_StoreNotFound_ThrowsException() {
        when(storeRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                crawlService.crawlGrabFoodMenu(999L, "https://example.com/menu"));

        verify(storeRepository).findById(999L);
        verify(productRepository, never()).saveAll(anyList());
    }
}
