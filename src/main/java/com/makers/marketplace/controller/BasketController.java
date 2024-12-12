package com.makers.marketplace.controller;

import com.makers.marketplace.model.Basket;
import com.makers.marketplace.model.BasketItem;
import com.makers.marketplace.model.Product;
import com.makers.marketplace.model.User;
import com.makers.marketplace.repository.BasketItemRepository;
import com.makers.marketplace.repository.BasketRepository;
import com.makers.marketplace.repository.ProductRepository;
import com.makers.marketplace.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/basket")
public class BasketController {

    @Autowired
    private BasketRepository basketRepository;

    @Autowired
    private BasketItemRepository basketItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    // Get basket items for a user
    @GetMapping("/{userId}")
    public ResponseEntity<List<BasketItem>> getBasketItems(@PathVariable Long userId) {
        Optional<Basket> basket = basketRepository.findByUserId(userId);
        if (basket.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        List<BasketItem> items = basketItemRepository.findByBasketId(basket.get().getId());
        return ResponseEntity.ok(items);
    }

    // Add item to basket
    @PostMapping("/{userId}/add")
    public ResponseEntity<BasketItem> addItemToBasket(@PathVariable Long userId, @RequestBody BasketItemRequest request) {
        Optional<User> userOptional = userRepository.findById(userId);
        Optional<Product> productOptional = productRepository.findById(request.getProductId());

        if (userOptional.isEmpty() || productOptional.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        User user = userOptional.get();
        Product product = productOptional.get();
        Basket basket = basketRepository.findByUserId(user.getId()).orElseGet(() -> basketRepository.save(new Basket(user)));

        BasketItem basketItem = new BasketItem(basket, product, request.getQuantity());
        basketItemRepository.save(basketItem);

        return ResponseEntity.ok(basketItem);
    }

    // Update quantity of a basket item
    @PutMapping("/{basketItemId}/update")
    public ResponseEntity<BasketItem> updateBasketItem(@PathVariable Long basketItemId, @RequestBody BasketItemRequest request) {
        Optional<BasketItem> basketItemOptional = basketItemRepository.findById(basketItemId);

        if (basketItemOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        BasketItem basketItem = basketItemOptional.get();
        basketItem.setQuantity(request.getQuantity());
        basketItemRepository.save(basketItem);

        return ResponseEntity.ok(basketItem);
    }

    // Clear basket for a user
    @DeleteMapping("/{userId}/clear")
    public ResponseEntity<Void> clearBasket(@PathVariable Long userId) {
        Optional<Basket> basket = basketRepository.findByUserId(userId);

        if (basket.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        basketItemRepository.deleteAll(basket.get().getItems());
        return ResponseEntity.noContent().build();
    }

    // Request body for adding/updating basket items
    public static class BasketItemRequest {
        private Long productId;
        private Integer quantity;

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
    }
}
