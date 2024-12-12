package com.makers.marketplace.controller;

import com.makers.marketplace.model.Order;
import com.makers.marketplace.model.OrderItem;
import com.makers.marketplace.model.Product;
import com.makers.marketplace.model.User;
import com.makers.marketplace.repository.OrderRepository;
import com.makers.marketplace.repository.UserRepository;
import com.makers.marketplace.service.OrderService;
import com.makers.marketplace.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductService productService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;
    @GetMapping("/create")
    public String showOrderForm(Model model) {
        // Fetch all products and add them to the model
        model.addAttribute("products", productService.getAllProducts());
        return "create_order";
    }

    @PostMapping("/create")
    public String createOrder(@RequestParam("productId") Long productId,
                              @RequestParam("quantity") Integer quantity) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User buyer = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Product product = productService.findById(productId);
        if (product == null) {
            // Handle product not found
            return "redirect:/products/all"; // Redirect to product list or error page
        }

        OrderItem orderItem = new OrderItem();
        orderItem.setProduct(product);
        orderItem.setQuantity(quantity);
        orderItem.setPrice(product.getPrice()); // Assuming price doesn't change at the time of order

        Order order = new Order();
        order.setBuyer(buyer);
        order.getOrderItems().add(orderItem);
        orderItem.setOrder(order); // Set the bidirectional relationship

        orderService.createOrder(order);

        return "redirect:/orders/my-orders";
    }

    @GetMapping("/my-orders")
    public String showMyOrders(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User buyer = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Order> orders = orderRepository.findByBuyer(buyer); // Now this will work
        model.addAttribute("orders", orders);
        return "my-orders";
    }

    // Additional methods to view order details, update status, etc.
    // ...
}