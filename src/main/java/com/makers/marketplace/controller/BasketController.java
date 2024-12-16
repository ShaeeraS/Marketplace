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

    // View the items in the basket
    @GetMapping("/view")
    public String viewBasket(Model model) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();

        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            model.addAttribute("error", "User not found");
            return "error"; // Render an error page if user is not found
        }

        User user = userOptional.get();
        Optional<Basket> basketOptional = basketRepository.findByUserId(user.getId());
        if (basketOptional.isEmpty()) {
            model.addAttribute("basketItems", List.of()); // If no basket exists
        } else {
            Basket basket = basketOptional.get();
            List<BasketItem> basketItems = basketItemRepository.findByBasketId(basket.getId());
            model.addAttribute("basketItems", basketItems);
        }

        return "basket"; // Return to the basket page
    }

    // Add product to basket
    @PostMapping("/add")
public String addToBasket(@RequestParam("productId") Long productId, Model model) {
    // Get the current authenticated user
    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();

    Optional<User> userOptional = userRepository.findByUsername(username);
    if (userOptional.isEmpty()) {
        model.addAttribute("error", "User not found");
        return "error"; // Render an error page if user is not found
    }

    User user = userOptional.get();
    Optional<Product> productOptional = productRepository.findById(productId);
    if (productOptional.isEmpty()) {
        model.addAttribute("error", "Product not found");
        return "error"; // Handle the case when the product is not found
    }

    Product product = productOptional.get();
    Optional<Basket> basketOptional = basketRepository.findByUserId(user.getId());
    Basket basket;
    if (basketOptional.isEmpty()) {
        // If the user doesn't have a basket, create one
        basket = new Basket();
        basket.setUser(user);
        basketRepository.save(basket);
    } else {
        basket = basketOptional.get();
    }

    BasketItem basketItem = new BasketItem();
    basketItem.setBasket(basket);
    basketItem.setProduct(product);
    basketItem.setQuantity(1); // Set default quantity as 1 or based on your business logic

    basketItemRepository.save(basketItem); // Save the BasketItem to the database

    return "redirect:/api/basket/view"; // Redirect to the basket view
}

    // Remove item from basket
    @PostMapping("/remove/{itemId}")
    public String removeFromBasket(@PathVariable Long itemId, Model model) {
        // Get the current authenticated user
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();

        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            model.addAttribute("error", "User not found");
            return "error"; // Render an error page if user is not found
        }

        User user = userOptional.get();
        Optional<BasketItem> basketItemOptional = basketItemRepository.findById(itemId);

        if (basketItemOptional.isEmpty()) {
            model.addAttribute("error", "Item not found in the basket");
            return "error";
        }

        BasketItem basketItem = basketItemOptional.get();

        // Ensure the basket item belongs to the logged-in user
        if (!basketItem.getBasket().getUser().getId().equals(user.getId())) {
            model.addAttribute("error", "You are not authorized to remove this item");
            return "error";
        }

        // Remove the item from the basket
        basketItemRepository.delete(basketItem);

        // Optionally, you can add a success message here
        model.addAttribute("success", "Item removed from basket");

        return "redirect:/api/basket/view"; // Redirect to the basket view
    }


}
