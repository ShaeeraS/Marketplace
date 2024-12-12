package com.makers.marketplace.service;

import com.makers.marketplace.model.Order;
import com.makers.marketplace.model.OrderItem;
import com.makers.marketplace.model.Product;
import com.makers.marketplace.repository.OrderItemRepository;
import com.makers.marketplace.repository.OrderRepository;
import com.makers.marketplace.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ProductRepository productRepository;

    public Order createOrder(Order order) {
        // Calculate total price from order items
        double totalPrice = order.getOrderItems().stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
        order.setTotalPrice(totalPrice);

        // Set default status to PENDING
        order.setStatus("PENDING");

        // Save the order
        Order savedOrder = orderRepository.save(order);

        // Update product status if needed
        for (OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
            // Example: Mark product as SOLD if it's the last item
            // You might need to adjust this logic based on your requirements
            if (product.getQuantity() <= item.getQuantity()) {
                product.setStatus("SOLD");
            } else {
                product.setQuantity(product.getQuantity() - item.getQuantity());
            }
            productRepository.save(product);
        }

        return savedOrder;
    }

    // Additional methods to get orders, update order status, etc.
    // ...
}