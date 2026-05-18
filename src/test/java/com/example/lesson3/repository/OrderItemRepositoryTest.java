package com.example.lesson3.repository;

import com.example.lesson3.model.Order;
import com.example.lesson3.model.OrderItem;
import com.example.lesson3.model.Product;
import com.example.lesson3.model.Store;
import com.example.lesson3.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class OrderItemRepositoryTest {

    @Autowired private OrderItemRepository orderItemRepository;
    @Autowired private OrderRepository orderRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private StoreRepository storeRepository;
    @Autowired private ProductRepository productRepository;

    @Test
    void testFindByOrderId() {
        User user = new User(); user.setEmail("x@example.com"); user.setUsername("x"); user.setPassword("p"); user.setRole(1);
        user = userRepository.save(user);
        Store store = new Store(); store.setName("Zeta Cafe"); store.setSlug("xxx"); store.setAddress("7 Zeta Ln"); store.setStatus(1); store = storeRepository.save(store);

        // create a valid product for FK and validation
        Product product = new Product(); product.setName("Latte"); product.setPrice(BigDecimal.valueOf(25.0)); product.setStatus(1); product.setStore(store);
        product = productRepository.save(product);

        Order order = new Order(); order.setUser(user); order.setStore(store); order.setStatus("paid"); order.setCreatedAt(LocalDateTime.now());
        order = orderRepository.save(order);

        OrderItem i1 = new OrderItem(); i1.setOrder(order); i1.setProduct(product); i1.setQuantity(1); i1.setPrice(BigDecimal.valueOf(2.0));
        OrderItem i2 = new OrderItem(); i2.setOrder(order); i2.setProduct(product); i2.setQuantity(3); i2.setPrice(BigDecimal.valueOf(1.5));
        orderItemRepository.save(i1); orderItemRepository.save(i2);

        List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
        assertEquals(2, items.size());
    }
}
