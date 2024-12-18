package com.makers.marketplace.controller;

import com.makers.marketplace.model.OrderItem;
import com.makers.marketplace.model.Product;
import com.makers.marketplace.model.User;
import com.makers.marketplace.repository.OrderItemRepository;
import com.makers.marketplace.repository.ProductRepository;
import com.makers.marketplace.repository.UserRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/order-items")
public class OrderItemController {

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public List<OrderItem> getAllOrderItems() {
        return orderItemRepository.findAll();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderItem>> getOrderItemsByUserId(@PathVariable Long userId) {
        List<OrderItem> orderItems = orderItemRepository.findByUserId(userId);
        if (orderItems.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(orderItems);
    }

    @PostMapping
    public ResponseEntity<OrderItem> createOrderItem(@RequestBody OrderItemRequest orderItemRequest) {
        Optional<User> userOptional = userRepository.findById(orderItemRequest.getUserId());
        Optional<Product> productOptional = productRepository.findById(orderItemRequest.getProductId());

        if (userOptional.isEmpty() || productOptional.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Product product = productOptional.get();
        User user = userOptional.get();
        Double price = product.getPrice() * orderItemRequest.getQuantity();

        OrderItem orderItem = new OrderItem(product, user.getId(), orderItemRequest.getQuantity(), price);
        OrderItem savedOrderItem = orderItemRepository.save(orderItem);

        return ResponseEntity.ok(savedOrderItem);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderItem> updateOrderItem(@PathVariable Long id, @RequestBody OrderItemRequest orderItemRequest) {
        Optional<OrderItem> orderItemOptional = orderItemRepository.findById(id);

        if (orderItemOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        OrderItem orderItem = orderItemOptional.get();
        orderItem.setQuantity(orderItemRequest.getQuantity());
        orderItem.setPrice(orderItemRequest.getPrice());

        OrderItem updatedOrderItem = orderItemRepository.save(orderItem);
        return ResponseEntity.ok(updatedOrderItem);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrderItem(@PathVariable Long id) {
        Optional<OrderItem> orderItemOptional = orderItemRepository.findById(id);

        if (orderItemOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        orderItemRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Data
    public static class OrderItemRequest {
        private Long userId;
        private Long productId;
        private Integer quantity;
        private Double price;
    }
}