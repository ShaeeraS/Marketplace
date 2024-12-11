package com.makers.marketplace.model;

import jakarta.persistence.*;

import java.io.Serializable;




    @Entity
    @Table(name = "orders")
    public class Order implements Serializable {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(unique = true, nullable = false)
        private Long sellerId;

        @Column(unique = true, nullable = false)
        private Long buyerId;


        @Column(unique = true, nullable = false)
        private Long itemId;




        @Column(nullable = false)
        private String email;



        // Constructors
        public Order() {}

        public Order(Long sellerId, Long buyerId, Long itemId) {
            this.sellerId = sellerId;
            this.buyerId = buyerId;
            this.itemId = itemId;
        }

        // Getters and Setters
        public Long getOrderId() {
            return id;
        }

        public void setOrderId(Long id) {
            this.id = id;
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

        public Long getItemId(){
            return itemId;
        }
        public void setItemId(Long itemId){
            this.itemId = itemId;
        }








    }

