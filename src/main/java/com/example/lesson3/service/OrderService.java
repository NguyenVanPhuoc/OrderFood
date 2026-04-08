package com.example.lesson3.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.criteria.Predicate;

import com.example.lesson3.model.Order;
import com.example.lesson3.model.OrderItem;
import com.example.lesson3.model.Product;
import com.example.lesson3.model.Store;
import com.example.lesson3.model.User;
import com.example.lesson3.repository.OrderRepository;
import com.example.lesson3.repository.ProductRepository;
import com.example.lesson3.repository.StoreRepository;
import com.example.lesson3.repository.OrderItemRepository;
import com.example.lesson3.request.OrderRequest;
import com.example.lesson3.request.OrderedProductDTO;

@Service
public class OrderService {

	private static final Logger log = LoggerFactory.getLogger(OrderService.class);

	private final OrderRepository orderRepository;
	private final ProductRepository productRepository;
	private final StoreRepository storeRepository;
	private final OrderItemRepository orderItemRepository;

	public OrderService(OrderRepository orderRepository, ProductRepository productRepository,
			StoreRepository storeRepository, OrderItemRepository orderItemRepository) {
		this.orderRepository = orderRepository;
		this.productRepository = productRepository;
		this.storeRepository = storeRepository;
		this.orderItemRepository = orderItemRepository;
	}

	public Page<Order> findAllWithFilter(String keyword, String status, Long userId,
			String startDate, String endDate, int page, int size) {
		Sort sort = Sort.by("id").descending();
		Pageable pageable = PageRequest.of(page - 1, size, sort);

		return orderRepository.findAll((root, query, cb) -> {
			List<Predicate> predicates = new ArrayList<>();

			if (keyword != null && !keyword.isEmpty()) {
				predicates.add(cb.like(cb.lower(root.get("store").get("name")),
						"%" + keyword.toLowerCase() + "%"));
			}
			if (status != null && !status.isEmpty()) {
				predicates.add(cb.equal(root.get("status"), status));
			}
			if (userId != null) {
				predicates.add(cb.equal(root.get("user").get("id"), userId));
			}
			if (startDate != null && !startDate.isEmpty()) {
				LocalDateTime startDateTime = LocalDate.parse(startDate).atStartOfDay();
				predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), startDateTime));
			}
			if (endDate != null && !endDate.isEmpty()) {
				LocalDateTime endDateTime = LocalDate.parse(endDate).atTime(LocalTime.MAX);
				predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), endDateTime));
			}

			return cb.and(predicates.toArray(new Predicate[0]));
		}, pageable);
	}

	@Transactional
	public void createOrder(User user, OrderRequest request) {
		if (request == null || request.getStoreId() == null || request.getItems() == null
				|| request.getItems().isEmpty()) {
			throw new RuntimeException("Invalid order request");
		}

		Store store = storeRepository.findById(request.getStoreId())
				.orElseThrow(() -> new RuntimeException("Store not found: " + request.getStoreId()));

		Order order = new Order();
		order.setUser(user);
		order.setStore(store);
		order.setStatus("unpaid");

		List<OrderItem> orderItems = new ArrayList<>();

		for (OrderRequest.OrderItemRequest itemReq : request.getItems()) {
			if (itemReq.getProductId() == null || itemReq.getQuantity() <= 0 || itemReq.getPrice() <= 0) {
				throw new RuntimeException("Invalid order item data");
			}

			Product product = productRepository.findById(itemReq.getProductId())
					.orElseThrow(() -> new RuntimeException("Product not found: " + itemReq.getProductId()));

			OrderItem item = new OrderItem();
			item.setOrder(order);
			item.setProduct(product);
			item.setQuantity(itemReq.getQuantity());
			item.setPrice(itemReq.getPrice());
			item.setNote(itemReq.getNote());
			item.setCreatedAt(LocalDateTime.now());
			item.setUpdatedAt(LocalDateTime.now());
			orderItems.add(item);
		}

		// Tính tổng tiền server-side thay vì tin vào client
		double serverTotal = orderItems.stream()
				.mapToDouble(i -> i.getPrice() * i.getQuantity())
				.sum();
		order.setTotalPrice(serverTotal);
		order.setOrderItems(orderItems);
		orderRepository.save(order);
		log.info("Order created: userId={}, storeId={}, total={}", user.getId(), store.getId(), serverTotal);
	}

	public List<OrderedProductDTO> findOrderedProductsToday(Long storeId) {
		LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
		LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);

		List<Order> orders = orderRepository.findByStoreIdAndCreatedAtBetween(storeId, startOfDay, endOfDay);

		List<OrderedProductDTO> result = new ArrayList<>();
		for (Order order : orders) {
			String customerName = order.getUser().getName();
			Long userId = order.getUser().getId();

			for (OrderItem item : order.getOrderItems()) {
				OrderedProductDTO dto = new OrderedProductDTO(item.getId(), item.getProduct().getName(),
						item.getQuantity(), customerName, userId, item.getNote());
				result.add(dto);
			}
		}
		return result;
	}

	public List<Order> findUnpaidOrdersByUser(Long userId) {
		return orderRepository.findByUserIdAndStatus(userId, "unpaid");
	}

	public void updateOrderStatus(Long orderId, String status) {
		Order order = orderRepository.findById(orderId)
				.orElseThrow(() -> new RuntimeException("Order not found"));
		order.setStatus(status);
		orderRepository.save(order);
	}

	public void deleteOrder(Long orderId) {
		Order order = orderRepository.findById(orderId)
				.orElseThrow(() -> new RuntimeException("Order not found"));
		orderRepository.delete(order);
	}

	public void deleteMultipleOrders(List<Long> orderIds) {
		List<Order> orders = orderRepository.findAllById(orderIds);
		orderRepository.deleteAll(orders);
	}

	public Order getOrderById(Long id) {
		return orderRepository.findById(id).orElse(null);
	}

	/**
	 * Kiểm tra xem user có quyền xóa orderItem này không (phải là chủ đơn hàng)
	 */
	public boolean canUserDeleteOrderItem(Long orderItemId, Long userId) {
		return orderItemRepository.findById(orderItemId)
				.map(item -> item.getOrder().getUser().getId().equals(userId))
				.orElse(false);
	}

	@Transactional
	public void deleteOrderItem(Long orderItemId) {
		OrderItem orderItem = orderItemRepository.findById(orderItemId)
				.orElseThrow(() -> new RuntimeException("Order item not found"));

		Order order = orderItem.getOrder();
		orderItemRepository.delete(orderItem);

		List<OrderItem> remainingItems = orderItemRepository.findByOrderId(order.getId());
		if (remainingItems.isEmpty()) {
			orderRepository.delete(order);
		} else {
			double newTotal = remainingItems.stream()
					.mapToDouble(item -> item.getPrice() * item.getQuantity())
					.sum();
			order.setTotalPrice(newTotal);
			orderRepository.save(order);
		}
	}

}
