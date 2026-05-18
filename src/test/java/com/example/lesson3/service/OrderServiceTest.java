package com.example.lesson3.service;

import com.example.lesson3.model.*;
import com.example.lesson3.repository.*;
import com.example.lesson3.request.OrderRequest;
import com.example.lesson3.request.OrderedProductDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class OrderServiceTest {

    @Mock private OrderRepository orderRepository;
    @Mock private ProductRepository productRepository;
    @Mock private StoreRepository storeRepository;
    @Mock private OrderItemRepository orderItemRepository;

    @InjectMocks private OrderService orderService;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateOrder_Success() {
        User user = new User(); user.setId(1L); user.setName("Alice");

        Store store = new Store(); store.setId(5L);

        OrderRequest req = new OrderRequest();
        req.setStoreId(5L);
        req.setTotal(100.0);
        List<OrderRequest.OrderItemRequest> items = new ArrayList<>();
        OrderRequest.OrderItemRequest item = new OrderRequest.OrderItemRequest();
        item.setProductId(7L);
        item.setQuantity(2);
        item.setPrice(10.0);
        item.setNote("no sugar");
        items.add(item);
        req.setItems(items);

        Product product = new Product();
        product.setId(7L);
        product.setPrice(BigDecimal.valueOf(10.0));
        product.setStore(store); // cross-store check cần product.getStore().getId() == storeId

        when(storeRepository.findById(5L)).thenReturn(Optional.of(store));
        when(productRepository.findById(7L)).thenReturn(Optional.of(product));

        orderService.createOrder(user, req);

        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void testCreateOrder_InvalidRequestThrows() {
        assertThrows(RuntimeException.class, () -> orderService.createOrder(new User(), null));

        OrderRequest bad = new OrderRequest(); bad.setStoreId(1L); bad.setItems(Collections.emptyList());
        assertThrows(RuntimeException.class, () -> orderService.createOrder(new User(), bad));
    }

    @Test
    void testFindOrderedProductsToday_Aggregates() {
        User u = new User(); u.setName("Bob");
        Product p = new Product(); p.setName("Juice");
        OrderItem oi = new OrderItem(); oi.setId(1L); oi.setProduct(p); oi.setQuantity(2); oi.setPrice(BigDecimal.valueOf(5.0)); oi.setNote("less ice");
        Order order = new Order(); order.setUser(u); order.setOrderItems(Arrays.asList(oi));
        when(orderRepository.findByStoreIdAndCreatedAtBetween(anyLong(), any(), any())).thenReturn(Arrays.asList(order));

        List<OrderedProductDTO> result = orderService.findOrderedProductsToday(2L);
        assertEquals(1, result.size());
        assertEquals("Juice", result.get(0).getProductName());
        assertEquals("Bob", result.get(0).getCustomerName());
    }

    @Test
    void testFindOrderedProductsToday_NullUserOrder_Skipped() {
        Order orderWithNoUser = new Order();
        orderWithNoUser.setUser(null);

        User u = new User(); u.setId(2L); u.setName("Bob");
        Product p = new Product(); p.setName("Tea");
        OrderItem oi = new OrderItem(); oi.setId(2L); oi.setProduct(p); oi.setQuantity(1); oi.setPrice(BigDecimal.valueOf(3.0)); oi.setNote("");
        Order orderWithUser = new Order(); orderWithUser.setUser(u); orderWithUser.setOrderItems(Arrays.asList(oi));

        when(orderRepository.findByStoreIdAndCreatedAtBetween(anyLong(), any(), any()))
            .thenReturn(Arrays.asList(orderWithNoUser, orderWithUser));

        List<OrderedProductDTO> result = orderService.findOrderedProductsToday(3L);

        assertEquals(1, result.size(), "Order với null user phải bị bỏ qua, không gây NPE");
        assertEquals("Tea", result.get(0).getProductName());
    }

    @Test
    void testFindUnpaidOrdersByUser() {
        when(orderRepository.findByUserIdAndStatus(1L, "unpaid")).thenReturn(Collections.emptyList());
        assertNotNull(orderService.findUnpaidOrdersByUser(1L));
    }

    @Test
    void testUpdateOrderStatus() {
        Order order = new Order(); order.setId(3L);
        when(orderRepository.findById(3L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        orderService.updateOrderStatus(3L, "paid");

        assertEquals("paid", order.getStatus());
        verify(orderRepository).save(order);
    }

    @Test
    void testDeleteOrder() {
        Order order = new Order();
        when(orderRepository.findById(4L)).thenReturn(Optional.of(order));
        orderService.deleteOrder(4L);
        verify(orderRepository).delete(order);
    }

    @Test
    void testDeleteMultipleOrders() {
        List<Order> orders = Arrays.asList(new Order(), new Order());
        when(orderRepository.findAllById(Arrays.asList(1L, 2L))).thenReturn(orders);
        orderService.deleteMultipleOrders(Arrays.asList(1L, 2L));
        verify(orderRepository).deleteAll(orders);
    }

    @Test
    void testGetOrderById() {
        Order order = new Order(); order.setId(9L);
        when(orderRepository.findById(9L)).thenReturn(Optional.of(order));
        assertEquals(9L, orderService.getOrderById(9L).getId());
    }

    @Test
    void testDeleteOrderItem_DeletesOrderWhenLastItem() {
        Order order = new Order(); order.setId(1L);
        OrderItem item = new OrderItem(); item.setId(10L); item.setOrder(order); item.setPrice(BigDecimal.valueOf(5.0)); item.setQuantity(1);
        when(orderItemRepository.findById(10L)).thenReturn(Optional.of(item));
        when(orderItemRepository.findByOrderId(1L)).thenReturn(Collections.emptyList());

        orderService.deleteOrderItem(10L);

        verify(orderRepository).delete(order);
        verify(orderRepository, never()).save(any());
    }

    @Test
    void testDeleteOrderItem_RecalculatesTotalWhenRemaining() {
        Order order = new Order(); order.setId(1L);
        OrderItem item = new OrderItem(); item.setId(10L); item.setOrder(order); item.setPrice(BigDecimal.valueOf(5.0)); item.setQuantity(1);
        when(orderItemRepository.findById(10L)).thenReturn(Optional.of(item));
        OrderItem remaining = new OrderItem(); remaining.setPrice(BigDecimal.valueOf(3.0)); remaining.setQuantity(2);
        when(orderItemRepository.findByOrderId(1L)).thenReturn(Arrays.asList(remaining));

        orderService.deleteOrderItem(10L);

        assertEquals(BigDecimal.valueOf(6.0), order.getTotalPrice());
        verify(orderRepository).save(order);
    }

    // Feature 3: findOrdersByUser — không lọc status
    @Test
    void testFindOrdersByUser_NoStatusFilter() {
        Page<Order> mockPage = new PageImpl<>(Arrays.asList(new Order(), new Order()));
        when(orderRepository.findByUserId(eq(1L), any(org.springframework.data.domain.Pageable.class)))
            .thenReturn(mockPage);

        Page<Order> result = orderService.findOrdersByUser(1L, null, 1, 10);

        assertEquals(2, result.getContent().size());
        verify(orderRepository).findByUserId(eq(1L), any(org.springframework.data.domain.Pageable.class));
    }

    // Feature 3: findOrdersByUser — lọc theo status
    @Test
    void testFindOrdersByUser_WithStatusFilter() {
        Order paid = new Order(); paid.setStatus("paid");
        Page<Order> mockPage = new PageImpl<>(Arrays.asList(paid));
        when(orderRepository.findByStatusAndUserId(eq("paid"), eq(1L), any(org.springframework.data.domain.Pageable.class)))
            .thenReturn(mockPage);

        Page<Order> result = orderService.findOrdersByUser(1L, "paid", 1, 10);

        assertEquals(1, result.getContent().size());
        assertEquals("paid", result.getContent().get(0).getStatus());
        verify(orderRepository).findByStatusAndUserId(eq("paid"), eq(1L), any(org.springframework.data.domain.Pageable.class));
    }

    // Feature 3: findOrdersByUser — status rỗng → không lọc
    @Test
    void testFindOrdersByUser_EmptyStatus_TreatedAsNoFilter() {
        Page<Order> mockPage = new PageImpl<>(Collections.emptyList());
        when(orderRepository.findByUserId(eq(2L), any(org.springframework.data.domain.Pageable.class)))
            .thenReturn(mockPage);

        Page<Order> result = orderService.findOrdersByUser(2L, "", 1, 10);

        verify(orderRepository).findByUserId(eq(2L), any(org.springframework.data.domain.Pageable.class));
        verify(orderRepository, never()).findByStatusAndUserId(any(), any(), any());
    }

    // Test canUserDeleteOrderItem — user là chủ đơn hàng
    @Test
    @SuppressWarnings("unchecked")
    void testCanUserDeleteOrderItem_OwnerCanDelete_ReturnsTrue() {
        User owner = new User(); owner.setId(10L);
        Order order = new Order(); order.setUser(owner);
        OrderItem item = new OrderItem(); item.setId(5L); item.setOrder(order);

        when(orderItemRepository.findById(5L)).thenReturn(Optional.of(item));

        assertTrue(orderService.canUserDeleteOrderItem(5L, 10L));
    }

    // Test canUserDeleteOrderItem — user khác → từ chối
    @Test
    void testCanUserDeleteOrderItem_DifferentUser_ReturnsFalse() {
        User owner = new User(); owner.setId(10L);
        Order order = new Order(); order.setUser(owner);
        OrderItem item = new OrderItem(); item.setId(5L); item.setOrder(order);

        when(orderItemRepository.findById(5L)).thenReturn(Optional.of(item));

        assertFalse(orderService.canUserDeleteOrderItem(5L, 99L));
    }

    // Test canUserDeleteOrderItem — item không tồn tại → false
    @Test
    void testCanUserDeleteOrderItem_ItemNotFound_ReturnsFalse() {
        when(orderItemRepository.findById(999L)).thenReturn(Optional.empty());

        assertFalse(orderService.canUserDeleteOrderItem(999L, 1L));
    }

    // Test findAllWithFilter — trả về Page qua Specification
    @Test
    @SuppressWarnings("unchecked")
    void testFindAllWithFilter_ReturnsFilteredPage() {
        Order o1 = new Order(); o1.setStatus("paid");
        Page<Order> mockPage = new PageImpl<>(Arrays.asList(o1));

        when(orderRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(mockPage);

        Page<Order> result = orderService.findAllWithFilter("pizza", "paid", 1L, null, null, 1, 10);

        assertEquals(1, result.getTotalElements());
        verify(orderRepository).findAll(any(Specification.class), any(Pageable.class));
    }

    // Test findAllWithFilter — không có tham số lọc
    @Test
    @SuppressWarnings("unchecked")
    void testFindAllWithFilter_NoFilters_ReturnsAllOrders() {
        Page<Order> mockPage = new PageImpl<>(Arrays.asList(new Order(), new Order()));

        when(orderRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(mockPage);

        Page<Order> result = orderService.findAllWithFilter(null, null, null, null, null, 1, 10);

        assertEquals(2, result.getTotalElements());
    }
}
