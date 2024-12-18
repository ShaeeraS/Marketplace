package com.makers.marketplace.controller;

import com.makers.marketplace.model.*;
import com.makers.marketplace.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
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

    @Autowired
    private OrderRepository orderRepository;

    @GetMapping("/view")
    public String viewBasket(Model model) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();

        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            model.addAttribute("error", "User not found");
            return "error";
        }

        User user = userOptional.get();
        Optional<Basket> basketOptional = basketRepository.findByUserId(user.getId());
        if (basketOptional.isEmpty()) {
            model.addAttribute("basketItems", List.of());
        } else {
            Basket basket = basketOptional.get();
            List<BasketItem> basketItems = basketItemRepository.findByBasketIdOrderByIdAsc(basket.getId());
            model.addAttribute("basketItems", basketItems);


        }

        return "basket";
    }

    @PostMapping("/add")
<<<<<<< HEAD
    public String addToBasket(@RequestParam("productId") Long productId, Model model) {
=======
    public String addToBasket(@RequestParam("productId") Long productId,
                              @RequestParam("quantity") Integer quantity,
                              Model model) {
>>>>>>> 6bccd5fbda32a7c60c7c2a5dbe89a088478afb61
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();

        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            model.addAttribute("error", "User not found");
            return "error";
        }

        User user = userOptional.get();
        Optional<Product> productOptional = productRepository.findById(productId);
        if (productOptional.isEmpty()) {
            model.addAttribute("error", "Product not found");
            return "error";
        }

        Product product = productOptional.get();

        // Check if requested quantity is <= product quantityAvailable
        if (quantity > product.getQuantityAvailable()) {
            model.addAttribute("error", "Requested quantity exceeds available stock");
            return "error";
        }

        Optional<Basket> basketOptional = basketRepository.findByUserId(user.getId());
        Basket basket;
        if (basketOptional.isEmpty()) {
            basket = new Basket();
            basket.setUser(user);
            basketRepository.save(basket);
        } else {
            basket = basketOptional.get();
        }

        BasketItem basketItem = new BasketItem();
        basketItem.setBasket(basket);
        basketItem.setProduct(product);
        basketItem.setQuantity(quantity);

        basketItemRepository.save(basketItem);

        return "redirect:/api/basket/view";
    }


    @PostMapping("/remove/{itemId}")
    public String removeFromBasket(@PathVariable Long itemId, Model model) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();

        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            model.addAttribute("error", "User not found");
            return "error";
        }

        User user = userOptional.get();
        Optional<BasketItem> basketItemOptional = basketItemRepository.findById(itemId);

        if (basketItemOptional.isEmpty()) {
            model.addAttribute("error", "Item not found in the basket");
            return "error";
        }

        BasketItem basketItem = basketItemOptional.get();

        if (!basketItem.getBasket().getUser().getId().equals(user.getId())) {
            model.addAttribute("error", "You are not authorized to remove this item");
            return "error";
        }

        basketItemRepository.delete(basketItem);
        model.addAttribute("success", "Item removed from basket");

        return "redirect:/api/basket/view";
    }

<<<<<<< HEAD
    @PostMapping("/checkout")
    public String checkout(Model model) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();
=======
    @PostMapping("/updateQuantity/{itemId}")
    public String updateBasketItemQuantity(@PathVariable Long itemId,
                                           @RequestParam("quantity") Integer newQuantity,
                                           Model model) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();

        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            model.addAttribute("error", "User not found");
            return "error";
        }

        User user = userOptional.get();

        Optional<BasketItem> basketItemOptional = basketItemRepository.findById(itemId);
        if (basketItemOptional.isEmpty()) {
            model.addAttribute("error", "Basket item not found");
            return "error";
        }

        BasketItem basketItem = basketItemOptional.get();

        // Check user ownership
        if (!basketItem.getBasket().getUser().getId().equals(user.getId())) {
            model.addAttribute("error", "You are not authorized to modify this item");
            return "error";
        }

        Product product = basketItem.getProduct();
        if (newQuantity > product.getQuantityAvailable()) {
            model.addAttribute("error", "Requested quantity exceeds available stock");
            return "error";
        }

        basketItem.setQuantity(newQuantity);
        basketItemRepository.save(basketItem);

        return "redirect:/api/basket/view";
    }
>>>>>>> 6bccd5fbda32a7c60c7c2a5dbe89a088478afb61

        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            model.addAttribute("error", "User not found");
            return "error";
        }

        User user = userOptional.get();
        Optional<Basket> basketOptional = basketRepository.findByUserId(user.getId());
        if (basketOptional.isEmpty()) {
            model.addAttribute("error", "Basket is empty");
            return "error";
        }

        Basket basket = basketOptional.get();
        List<BasketItem> basketItems = basketItemRepository.findByBasketId(basket.getId());

        if (basketItems.isEmpty()) {
            model.addAttribute("error", "Basket is empty");
            return "error";
        }

        Double totalPrice = basketItems.stream().mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity()).sum();
        List<OrderItem> orderItems = basketItems.stream()
                .map(item -> new OrderItem(item.getProduct(), user.getId(), item.getQuantity(), item.getProduct().getPrice() * item.getQuantity()))
                .toList();
        Order order = new Order(user.getId(), orderItems, totalPrice);
        Order savedOrder = orderRepository.save(order);

        for (BasketItem basketItem : basketItems) {
            Product product = basketItem.getProduct();
            product.setTotalQuantityOrdered(product.getTotalQuantityOrdered() + basketItem.getQuantity());
            productRepository.save(product);
        }

        basketItemRepository.deleteAll(basketItems);
        basketRepository.delete(basket);

        return "redirect:/api/orders/thanks?orderId=" + savedOrder.getId();
    }
}