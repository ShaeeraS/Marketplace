package com.makers.marketplace.controller;

import com.makers.marketplace.model.Order;
import com.makers.marketplace.model.OrderItem;
import com.makers.marketplace.model.User;
import com.makers.marketplace.repository.OrderRepository;
import com.makers.marketplace.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @GetMapping("/thanks")
    public String thank(@RequestParam Long orderId, Model model) {
        Optional<Order> orderOptional = orderRepository.findById(orderId);
        if (orderOptional.isEmpty()) {
            return "redirect:/";
        }
        Order order = orderOptional.get();
        model.addAttribute("order", order);
        model.addAttribute("groupedOrderItems", order.getGroupedOrderItems());
        return "thanks";
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        Optional<Order> orderOptional = orderRepository.findById(id);
        if (orderOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(orderOptional.get());
    }

    @GetMapping("/to-fulfill")
    public String getOrdersToFulfill(Model model) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();

        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            model.addAttribute("error", "User not found");
            return "error";
        }

        List<Order> ordersToFulfill = orderRepository.findAll().stream()
                .filter(order -> order.getOrderItems().stream()
                        .anyMatch(item -> item.getProduct().getUser().getId().equals(user.getId())))
                .filter(order -> !order.isDeclined() && !order.isFulfilled())
                .collect(Collectors.toList());

        ordersToFulfill.forEach(order -> {
            double totalPrice = order.getOrderItems().stream()
                    .mapToDouble(item -> item.getPrice() * item.getQuantity())
                    .sum();
            order.setTotalPrice(totalPrice);
        });

        model.addAttribute("orders", ordersToFulfill);
        return "orders_to_fulfill";
    }

    @PostMapping
    public ResponseEntity<String> createOrder(@RequestBody List<OrderItem> orderItems) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();

        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        User user = userOptional.get();
        Double totalPrice = orderItems.stream().mapToDouble(OrderItem::getPrice).sum();

        Order order = new Order(user.getId(), orderItems, totalPrice);
        Order savedOrder = orderRepository.save(order);

        return ResponseEntity.ok("redirect:/api/orders/thanks?orderId=" + savedOrder.getId());
    }

    @PostMapping("/fulfill")
    public String fulfillOrder(@RequestParam Long orderId, Model model) {
        Optional<Order> orderOptional = orderRepository.findById(orderId);
        if (orderOptional.isEmpty()) {
            model.addAttribute("error", "Order not found");
            return "error";
        }

        Order order = orderOptional.get();
        order.setFulfilled(true);
        orderRepository.save(order);
        return "redirect:/api/orders/to-fulfill";
    }
    @PostMapping("/decline")
    public String declineOrder(@RequestParam Long orderId, Model model) {
        Optional<Order> orderOptional = orderRepository.findById(orderId);
        if (orderOptional.isEmpty()) {
            model.addAttribute("error", "Order not found");
            return "error";
        }
        Order order = orderOptional.get();
        order.setDeclined(true);
        orderRepository.save(order);
        return "redirect:/api/orders/to-fulfill";
    }
}