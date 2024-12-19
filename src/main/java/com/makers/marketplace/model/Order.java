package com.makers.marketplace.model;

import jakarta.persistence.*;
import lombok.*;


import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems;

    @Column(nullable = false)
    private Double totalPrice;

    @Setter
    private boolean fulfilled;

    @Setter
    private boolean declined;


    public Order(Long userId, List<OrderItem> orderItems, Double totalPrice) {
        this.userId = userId;
        this.orderItems = orderItems;
        this.totalPrice = totalPrice;
        orderItems.forEach(item -> item.setOrder(this));
    }


    public Map<Product, Integer> getGroupedOrdertems() {
        return orderItems.stream()
                .collect(Collectors.groupingBy(OrderItem::getProduct, Collectors.summingInt(OrderItem::getQuantity)));
    }

}