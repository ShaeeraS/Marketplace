package com.makers.marketplace.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.makers.marketplace.model.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
}