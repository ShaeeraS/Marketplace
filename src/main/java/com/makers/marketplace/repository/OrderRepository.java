package com.makers.marketplace.repository;

import com.makers.marketplace.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface OrderRepository extends JpaRepository<User, Long> {
    Optional<User> findByOrderId(long orderId);
    boolean existsByOrderIde(long orderId);
}
