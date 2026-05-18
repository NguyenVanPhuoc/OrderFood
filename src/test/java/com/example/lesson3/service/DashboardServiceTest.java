package com.example.lesson3.service;

import com.example.lesson3.model.Order;
import com.example.lesson3.repository.OrderItemRepository;
import com.example.lesson3.repository.OrderRepository;
import com.example.lesson3.repository.StoreRepository;
import com.example.lesson3.repository.UserRepository;
import com.example.lesson3.request.DashboardStatsDTO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class DashboardServiceTest {

    @Mock private OrderRepository orderRepository;
    @Mock private OrderItemRepository orderItemRepository;
    @Mock private UserRepository userRepository;
    @Mock private StoreRepository storeRepository;

    @InjectMocks private DashboardService dashboardService;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetStats_ReturnsCounts() {
        when(orderRepository.count()).thenReturn(10L);
        when(orderRepository.countByStatus("paid")).thenReturn(6L);
        when(orderRepository.countByStatus("unpaid")).thenReturn(4L);
        when(orderRepository.sumTotalPriceByStatus("paid")).thenReturn(500000.0);
        when(userRepository.count()).thenReturn(5L);
        when(storeRepository.count()).thenReturn(3L);
        when(orderRepository.findByCreatedAtGreaterThanEqual(any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());
        when(orderItemRepository.findTopProducts(any(Pageable.class)))
                .thenReturn(Collections.emptyList());

        DashboardStatsDTO stats = dashboardService.getStats();

        assertEquals(10L, stats.getTotalOrders());
        assertEquals(6L, stats.getPaidOrders());
        assertEquals(4L, stats.getUnpaidOrders());
        assertEquals(500000.0, stats.getTotalRevenue());
        assertEquals(5L, stats.getTotalUsers());
        assertEquals(3L, stats.getTotalStores());
    }

    @Test
    void testGetStats_ChartLabelsHas7Days() {
        when(orderRepository.count()).thenReturn(0L);
        when(orderRepository.countByStatus(any())).thenReturn(0L);
        when(orderRepository.sumTotalPriceByStatus(any())).thenReturn(null);
        when(userRepository.count()).thenReturn(0L);
        when(storeRepository.count()).thenReturn(0L);
        when(orderRepository.findByCreatedAtGreaterThanEqual(any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());
        when(orderItemRepository.findTopProducts(any(Pageable.class)))
                .thenReturn(Collections.emptyList());

        DashboardStatsDTO stats = dashboardService.getStats();

        assertEquals(7, stats.getChartLabels().size());
        assertEquals(7, stats.getChartOrderCounts().size());
        // Tất cả 0 vì không có đơn hàng
        assertTrue(stats.getChartOrderCounts().stream().allMatch(c -> c == 0L));
    }

    @Test
    void testGetStats_RevenueNullHandled() {
        when(orderRepository.count()).thenReturn(0L);
        when(orderRepository.countByStatus(any())).thenReturn(0L);
        when(orderRepository.sumTotalPriceByStatus("paid")).thenReturn(null); // no paid orders
        when(userRepository.count()).thenReturn(0L);
        when(storeRepository.count()).thenReturn(0L);
        when(orderRepository.findByCreatedAtGreaterThanEqual(any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());
        when(orderItemRepository.findTopProducts(any(Pageable.class)))
                .thenReturn(Collections.emptyList());

        DashboardStatsDTO stats = dashboardService.getStats();

        // Phải trả về 0.0, không được NPE
        assertEquals(0.0, stats.getTotalRevenue());
    }

    @Test
    void testGetStats_OrdersCountedPerDay() {
        Order order = new Order();
        order.setCreatedAt(LocalDateTime.now());

        when(orderRepository.count()).thenReturn(1L);
        when(orderRepository.countByStatus(any())).thenReturn(0L);
        when(orderRepository.sumTotalPriceByStatus(any())).thenReturn(0.0);
        when(userRepository.count()).thenReturn(0L);
        when(storeRepository.count()).thenReturn(0L);
        when(orderRepository.findByCreatedAtGreaterThanEqual(any(LocalDateTime.class)))
                .thenReturn(Arrays.asList(order));
        when(orderItemRepository.findTopProducts(any(Pageable.class)))
                .thenReturn(Collections.emptyList());

        DashboardStatsDTO stats = dashboardService.getStats();

        // Đơn hàng hôm nay phải được đếm ở slot cuối (ngày hôm nay)
        List<Long> counts = stats.getChartOrderCounts();
        assertEquals(1L, counts.get(counts.size() - 1));
    }

    @Test
    void testGetStats_TopProductsPopulated() {
        Object[] row = new Object[]{"Bún bò", 15L};

        when(orderRepository.count()).thenReturn(0L);
        when(orderRepository.countByStatus(any())).thenReturn(0L);
        when(orderRepository.sumTotalPriceByStatus(any())).thenReturn(0.0);
        when(userRepository.count()).thenReturn(0L);
        when(storeRepository.count()).thenReturn(0L);
        when(orderRepository.findByCreatedAtGreaterThanEqual(any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());
        List<Object[]> topList = new java.util.ArrayList<>();
        topList.add(row);
        when(orderItemRepository.findTopProducts(any(Pageable.class)))
                .thenReturn(topList);

        DashboardStatsDTO stats = dashboardService.getStats();

        assertEquals(1, stats.getTopProductNames().size());
        assertEquals("Bún bò", stats.getTopProductNames().get(0));
        assertEquals(15L, stats.getTopProductQuantities().get(0));
    }
}
