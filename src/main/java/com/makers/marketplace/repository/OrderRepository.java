package com.makers.marketplace.repository;

import com.makers.marketplace.model.Order;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @EntityGraph(attributePaths = "orderItems")
    Optional<Order> findById(Long id);

    List<Order> findByUserId(Long id);
}
