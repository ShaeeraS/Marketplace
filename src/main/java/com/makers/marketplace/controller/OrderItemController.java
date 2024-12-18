package com.makers.marketplace.controller;

import com.makers.marketplace.model.OrderItem;
import com.makers.marketplace.model.Product;
import com.makers.marketplace.model.User;
import com.makers.marketplace.repository.OrderItemRepository;
import com.makers.marketplace.repository.ProductRepository;
import com.makers.marketplace.repository.UserRepository;
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

    // GET: Get all order items
    @GetMapping
    public List<OrderItem> getAllOrderItems() {
        return orderItemRepository.findAll();
    }

    // GET: Get all order items for a specific user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderItem>> getOrderItemsByUserId(@PathVariable Long userId) {
        List<OrderItem> orderItems = orderItemRepository.findByUserId(userId);
        if (orderItems.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(orderItems);
    }

    // POST: Create a new order item
    @PostMapping
    public ResponseEntity<OrderItem> createOrderItem(@RequestBody OrderItemRequest orderItemRequest) {
        Optional<User> userOptional = userRepository.findById(orderItemRequest.getUserId());
        Optional<Product> productOptional = productRepository.findById(orderItemRequest.getProductId());

        if (userOptional.isEmpty() || productOptional.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Product product = productOptional.get();
        User user = userOptional.get();

        // Calculate total price (price * quantity)
        Double price = product.getPrice() * orderItemRequest.getQuantity();

        // Create and save the order item
        OrderItem orderItem = new OrderItem(product, user, orderItemRequest.getQuantity(), price);
        OrderItem savedOrderItem = orderItemRepository.save(orderItem);

        return ResponseEntity.ok(savedOrderItem);
    }

    // PUT: Update an existing order item
    @PutMapping("/{id}")
    public ResponseEntity<OrderItem> updateOrderItem(@PathVariable Long id, @RequestBody OrderItemRequest orderItemRequest) {
        Optional<OrderItem> orderItemOptional = orderItemRepository.findById(id);

        if (orderItemOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        OrderItem orderItem = orderItemOptional.get();
        orderItem.setQuantity(orderItemRequest.getQuantity());
        orderItem.setPrice(orderItemRequest.getPrice()); // This line requires the getPrice() method in OrderItemRequest

        OrderItem updatedOrderItem = orderItemRepository.save(orderItem);
        return ResponseEntity.ok(updatedOrderItem);
    }

    // DELETE: Delete an order item
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrderItem(@PathVariable Long id) {
        Optional<OrderItem> orderItemOptional = orderItemRepository.findById(id);

        if (orderItemOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        orderItemRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // **OrderItemRequest**: This is the DTO (Data Transfer Object) for order item requests
    public static class OrderItemRequest {
        private Long userId;
        private Long productId;
        private Integer quantity;
        private Double price; // <-- Added price field here

        // Getters and Setters
        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public Long getProductId() {
            return productId;
        }

        public void setProductId(Long productId) {
            this.productId = productId;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }

        public Double getPrice() { // <-- Getter for price
            return price;
        }

        public void setPrice(Double price) { // <-- Setter for price
            this.price = price;
        }
    }
}
