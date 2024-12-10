package com.makers.marketplace.model;

import jakarta.persistence.*;

import java.io.Serializable;




    @Entity
    @Table(name = "orders")
    public class Order implements Serializable {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long orderId;

        @Column(unique = true, nullable = false)
        private Long sellerId;

        @Column(unique = true, nullable = false)
        private Long buyerId;



        @Column(nullable = false)
        private String email;



        // Constructors
        public Order() {}

        public Order(Long sellerId, Long buyerId) {
            this.sellerId = sellerId;
            this.buyerId = buyerId;
        }

        // Getters and Setters
        public Long getOrderId() {
            return orderId;
        }

        public void setOrderId(Long id) {
            this.orderId = id;
        }

        public Long getBuyerId() {
            return buyerId;
        }

        public void setBuyerId(Long buyerId) {
            this.buyerId = buyerId;
        }

        public Long getSellerId() {
            return sellerId;
        }
        public void setSellerIdd(Long sellerId) {
            this.sellerId = sellerId;
        }







    }

