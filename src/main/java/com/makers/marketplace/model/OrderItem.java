package com.makers.marketplace.model;

import com.makers.marketplace.model.Order;
import com.makers.marketplace.model.Product;
import com.makers.marketplace.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "order_items")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Double price;

    public OrderItem() {}

    public OrderItem(Product product, User user, Integer quantity, Double price) {
        this.product = product;
        this.user = user;
        this.quantity = quantity;
        this.price = price;
    }

}