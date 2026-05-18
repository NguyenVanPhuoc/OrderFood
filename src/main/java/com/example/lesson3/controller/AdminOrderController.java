package com.example.lesson3.controller;

import com.example.lesson3.model.Order;
import com.example.lesson3.model.User;
import com.example.lesson3.service.OrderService;
import com.example.lesson3.service.UserService;

import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller

@RequestMapping("/admin/orders")
public class AdminOrderController {

    private static final Logger log = LoggerFactory.getLogger(AdminOrderController.class);

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String listOrders(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate,
            Model model) {

        Page<Order> orderPage = orderService.findAllWithFilter(keyword, status, userId, startDate, endDate, page, size);
        List<User> users = userService.getAllUsers();

        model.addAttribute("orders", orderPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", orderPage.getTotalPages());
        model.addAttribute("totalItems", orderPage.getTotalElements());
        model.addAttribute("size", size);
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        model.addAttribute("userId", userId);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("users", users);
        model.addAttribute("contentPage", "/WEB-INF/views/orders/list.jsp");
        model.addAttribute("pageTitle", "Danh sách đơn hàng");
        model.addAttribute("currentPath", "/admin/orders");
        return "layouts/main";
    }

    @PostMapping("/{id}/status")
    public String updateOrderStatus(@PathVariable Long id, @RequestParam String status, RedirectAttributes redirectAttributes) {
        try {
            orderService.updateOrderStatus(id, status);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật trạng thái đơn hàng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra khi cập nhật trạng thái đơn hàng!");
        }
        return "redirect:/admin/orders";
    }

    @PostMapping("/delete/{id}")
    public String deleteOrder(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            orderService.deleteOrder(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa đơn hàng thành công!");
        } catch (Exception e) {
            log.error("Error deleting order id={}", id, e);
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra khi xóa đơn hàng.");
        }
        return "redirect:/admin/orders";
    }

    @PostMapping("/delete-multiple")
    public String deleteMultipleOrders(@RequestParam("itemIds") String itemIds, RedirectAttributes redirectAttributes) {
        if (itemIds == null || itemIds.isBlank()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Chưa chọn đơn hàng nào để xóa.");
            return "redirect:/admin/orders";
        }
        try {
            List<Long> ids = Arrays.stream(itemIds.split(","))
                    .filter(s -> !s.isBlank())
                    .map(Long::parseLong)
                    .collect(Collectors.toList());
            orderService.deleteMultipleOrders(ids);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa các đơn hàng thành công!");
        } catch (Exception e) {
            log.error("Error deleting multiple orders", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra khi xóa đơn hàng.");
        }
        return "redirect:/admin/orders";
    }

    @GetMapping("/{id}/items")
    @ResponseBody
    public ResponseEntity<?> getOrderItems(@PathVariable Long id) {
        try {
            Order order = orderService.getOrderById(id);
            if (order == null) {
                return ResponseEntity.notFound().build();
            }

            Map<String, Object> response = new HashMap<>();
            response.put("customerName", order.getUser() != null ? order.getUser().getName() : "Ẩn danh");
            response.put("storeName", order.getStore().getName());
            response.put("orderDate", order.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            response.put("status", order.getStatus());
            response.put("totalPrice", order.getTotalPrice());

            List<Map<String, Object>> items = order.getOrderItems().stream()
                .map(item -> {
                    Map<String, Object> itemMap = new HashMap<>();
                    itemMap.put("productName", item.getProduct().getName());
                    itemMap.put("quantity", item.getQuantity());
                    itemMap.put("price", item.getPrice());
                    itemMap.put("note", item.getNote());
                    return itemMap;
                })
                .collect(Collectors.toList());
            response.put("items", items);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching order detail for id={}", id, e);
            return ResponseEntity.badRequest().body("Có lỗi xảy ra khi lấy thông tin đơn hàng.");
        }
    }
}

