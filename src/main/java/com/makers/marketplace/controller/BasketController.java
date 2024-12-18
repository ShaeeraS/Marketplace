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
            List<BasketItem> basketItems = basketItemRepository.findByBasketId(basket.getId());
            model.addAttribute("basketItems", basketItems);
        }

        return "basket";
    }

    @PostMapping("/add")
    public String addToBasket(@RequestParam("productId") Long productId,
                              @RequestParam("quantity") Integer quantity,
                              Model model) {
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



}
