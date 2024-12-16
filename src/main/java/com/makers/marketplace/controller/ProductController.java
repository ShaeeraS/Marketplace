package com.makers.marketplace.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.makers.marketplace.model.Product;
import com.makers.marketplace.model.User;
import com.makers.marketplace.repository.UserRepository;
import com.makers.marketplace.service.ProductService;

import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/new")
    public String showCreateProductForm(Model model) {
        model.addAttribute("product", new Product());
        return "product_form";
    }

    @PostMapping("/new")
    public String createProduct(@ModelAttribute("product") Product product) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        product.setUser(user);
        productService.createProduct(product);
        return "redirect:/dashboard";
    }
    @GetMapping("/all")
    public String showAllProducts(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        List<Product> products = productService.findAll();
        model.addAttribute("products", products);
        return "products";
    }
    @GetMapping("/{id}")
    public String showProduct(Model model, @PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Product product = productService.findById(id);
        model.addAttribute("product", product);
        return "product";
    }

    @GetMapping("/my")
    public String listUserProducts(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Product> userProducts = productService.findProductsByUserId(user.getId());

        model.addAttribute("products", userProducts);
        model.addAttribute("username", username);

        return "my_products";
    }

    @PostMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Product product = productService.findById(id);
        if (product == null) {
            model.addAttribute("errorMessage", "Product not found.");
            return "redirect:/products/my?error=ProductNotFound";
        }

        if (!product.getUser().getId().equals(user.getId())) {
            // Handle unauthorized deletion attempt
            model.addAttribute("errorMessage", "You are not authorized to delete this product.");
            return "redirect:/products/my?error=Unauthorized";
        }

        productService.deleteProductById(id);

        return "redirect:/products/my?success=ProductDeleted";
    }


}
