package com.makers.marketplace.repository;

import com.makers.marketplace.model.BasketItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BasketItemRepository extends JpaRepository<BasketItem, Long> {
    List<BasketItem> findByBasketId(Long basketId);
    List<BasketItem> findByBasket_UserId(Long userId);
    Optional<BasketItem> findById(Long id);  // Optional method to find by id
    List<BasketItem> findByBasketIdOrderByIdAsc(Long basketId);

}
