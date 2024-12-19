package com.makers.marketplace.controller;

import com.makers.marketplace.model.User;
import com.makers.marketplace.model.Order;
import com.makers.marketplace.repository.OrderRepository;
import com.makers.marketplace.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/")
    public String homeRedirect() {
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        model.addAttribute("username", username);
        return "dashboard";
    }

    @GetMapping("/profile")
    public String viewProfile(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        model.addAttribute("username", username);
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            model.addAttribute("error", "User not found");
            return "error";
        }
        long fulfilledOrdersCount = orderRepository.findAll().stream()
                .filter(order -> order.getOrderItems().stream()
                        .anyMatch(item -> item.getProduct().getUser().getId().equals(user.getId())))
                .filter(Order::isFulfilled)
                .count();
        long declinedOrdersCount = orderRepository.findAll().stream()
                .filter(order -> order.getOrderItems().stream()
                        .anyMatch(item -> item.getProduct().getUser().getId().equals(user.getId())))
                .filter(order -> !order.isFulfilled())
                .count();
        model.addAttribute("fulfilledOrdersCount", fulfilledOrdersCount);
        model.addAttribute("declinedOrdersCount", declinedOrdersCount);

        return "profile";
    }
}