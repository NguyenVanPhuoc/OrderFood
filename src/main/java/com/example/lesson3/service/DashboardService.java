package com.example.lesson3.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.example.lesson3.model.Order;
import com.example.lesson3.repository.OrderItemRepository;
import com.example.lesson3.repository.OrderRepository;
import com.example.lesson3.repository.StoreRepository;
import com.example.lesson3.repository.UserRepository;
import com.example.lesson3.request.DashboardStatsDTO;

@Service
public class DashboardService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;

    public DashboardService(OrderRepository orderRepository, OrderItemRepository orderItemRepository,
            UserRepository userRepository, StoreRepository storeRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.userRepository = userRepository;
        this.storeRepository = storeRepository;
    }

    public DashboardStatsDTO getStats() {
        DashboardStatsDTO stats = new DashboardStatsDTO();

        // Tổng quan
        stats.setTotalOrders(orderRepository.count());
        stats.setPaidOrders(orderRepository.countByStatus("paid"));
        stats.setUnpaidOrders(orderRepository.countByStatus("unpaid"));

        Double revenue = orderRepository.sumTotalPriceByStatus("paid");
        stats.setTotalRevenue(revenue != null ? revenue : 0.0);

        stats.setTotalUsers(userRepository.count());
        stats.setTotalStores(storeRepository.count());

        // Đơn hàng theo ngày — 7 ngày gần nhất
        LocalDate today = LocalDate.now();
        LocalDateTime startDate = today.minusDays(6).atStartOfDay();
        List<Order> recentOrders = orderRepository.findByCreatedAtGreaterThanEqual(startDate);

        DateTimeFormatter dayFmt = DateTimeFormatter.ofPattern("dd/MM");
        Map<LocalDate, Long> ordersPerDay = new LinkedHashMap<>();
        List<String> labels = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate day = today.minusDays(i);
            ordersPerDay.put(day, 0L);
            labels.add(day.format(dayFmt));
        }
        for (Order order : recentOrders) {
            if (order.getCreatedAt() != null) {
                LocalDate day = order.getCreatedAt().toLocalDate();
                ordersPerDay.computeIfPresent(day, (k, v) -> v + 1);
            }
        }

        stats.setChartLabels(labels);
        stats.setChartOrderCounts(new ArrayList<>(ordersPerDay.values()));

        // Top 5 sản phẩm bán chạy
        List<Object[]> topRows = orderItemRepository.findTopProducts(PageRequest.of(0, 5));
        List<String> productNames = new ArrayList<>();
        List<Long> productQtys = new ArrayList<>();
        for (Object[] row : topRows) {
            productNames.add((String) row[0]);
            productQtys.add(((Number) row[1]).longValue());
        }
        stats.setTopProductNames(productNames);
        stats.setTopProductQuantities(productQtys);

        return stats;
    }
}
