package com.makers.marketplace.repository;

import com.makers.marketplace.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    // Find all order items for a specific user
    List<OrderItem> findByUserId(Long userId);
    
    // Find all order items for a specific product
    List<OrderItem> findByProductId(Long productId);
}
