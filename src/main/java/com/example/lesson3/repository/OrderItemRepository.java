package com.example.lesson3.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.lesson3.model.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrderId(Long orderId);

    // [name, totalQuantity] — top sản phẩm bán chạy cho Dashboard
    @Query("SELECT oi.product.name, SUM(oi.quantity) AS totalQty FROM OrderItem oi GROUP BY oi.product.id, oi.product.name ORDER BY totalQty DESC")
    List<Object[]> findTopProducts(Pageable pageable);
}
