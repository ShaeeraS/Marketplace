package com.makers.marketplace.controller;

import com.makers.marketplace.model.*;
import com.makers.marketplace.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private BasketRepository basketRepository;

    @Autowired
    private BasketItemRepository basketItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @PostMapping("/create")
    public String createOrder(Model model) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Basket basket = basketRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Basket not found"));

        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now()); // Set the order date

        List<BasketItem> basketItems = basketItemRepository.findByBasketId(basket.getId());
        for (BasketItem basketItem : basketItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(basketItem.getProduct());
            orderItem.setQuantity(basketItem.getQuantity());
            orderItem.setPrice(basketItem.getProduct().getPrice() * basketItem.getQuantity()); // Calculate price
            order.getOrderItems().add(orderItem); // Add order item to the order
        }

        basketItemRepository.deleteAll(basketItems); // Clear the basket
        orderRepository.save(order); // Save the order (this will also save order items)

        model.addAttribute("success", "Order created successfully!");
        return "redirect:/orders/active";
    }

    @GetMapping("/active")
    public String getActiveOrders(Model model) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Order> orders = orderRepository.findByUserId(user.getId());
        model.addAttribute("orders", orders);

        return "active_orders";
    }

    @GetMapping("/history")
    public String getOrderHistory(Model model) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<OrderItem> soldItems = orderItemRepository.findByUserId(user.getId());
        model.addAttribute("soldItems", soldItems);

        return "order_history";
    }
}