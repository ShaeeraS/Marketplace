package com.makers.marketplace.controller;

import com.makers.marketplace.model.Order;
import com.makers.marketplace.model.OrderItem;
import com.makers.marketplace.model.Product;
import com.makers.marketplace.model.User;
import com.makers.marketplace.repository.OrderItemRepository;
import com.makers.marketplace.repository.OrderRepository;
import com.makers.marketplace.repository.ProductRepository;
import com.makers.marketplace.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    // Create a new order
    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        User buyer = userRepository.findById(order.getBuyer().getId()).orElse(null);
        if (buyer == null) {
            return ResponseEntity.badRequest().body(null);
        }

        List<OrderItem> orderItems = new ArrayList<>();
        double totalPrice = 0;

        for (OrderItem item : order.getOrderItems()) {
            Product product = productRepository.findById(item.getProduct().getId()).orElse(null);
            if (product == null) {
                return ResponseEntity.badRequest().body(null);
            }

            item.setOrder(order);
            item.setPrice(product.getPrice()); // Store the current product price
            totalPrice += item.getPrice() * item.getQuantity();
            orderItems.add(item);
        }

        order.setOrderItems(orderItems);
        order.setTotalPrice(totalPrice);
        order.setStatus("PENDING");

        Order savedOrder = orderRepository.save(order);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedOrder);
    }

    // ... other controller methods (getOrdersByBuyer, etc.)
}