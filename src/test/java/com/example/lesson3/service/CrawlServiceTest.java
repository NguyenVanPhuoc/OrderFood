package com.example.lesson3.service;

import com.example.lesson3.repository.ProductRepository;
import com.example.lesson3.repository.StoreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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

    // Test extractMerchantId — URL bình thường
    @Test
    void testExtractMerchantId_NormalUrl_ReturnsLastSegment() {
        String result = crawlService.extractMerchantId(
                "https://food.grab.com/vn/vi/restaurant/bun-bo-hue-ABCD1234");
        assertEquals("bun-bo-hue-ABCD1234", result);
    }

    // Test extractMerchantId — URL kết thúc bằng /items
    @Test
    void testExtractMerchantId_UrlEndingWithItems_StripsItemsSegment() {
        String result = crawlService.extractMerchantId(
                "https://food.grab.com/vn/vi/restaurant/my-restaurant-XYZ789/items");
        assertEquals("my-restaurant-XYZ789", result);
    }

    // Test extractMerchantId — URL có query string
    @Test
    void testExtractMerchantId_UrlWithQueryParams_IgnoresQuery() {
        String result = crawlService.extractMerchantId(
                "https://food.grab.com/vn/vi/restaurant/shop-123?ref=home&tab=menu");
        assertEquals("shop-123", result);
    }

    // Test extractMerchantId — null → null
    @Test
    void testExtractMerchantId_NullInput_ReturnsNull() {
        assertNull(crawlService.extractMerchantId(null));
    }

    // Test extractMerchantId — segment quá ngắn → null
    @Test
    void testExtractMerchantId_ShortSegment_ReturnsNull() {
        String result = crawlService.extractMerchantId("https://food.grab.com/ab");
        assertNull(result);
    }
}
