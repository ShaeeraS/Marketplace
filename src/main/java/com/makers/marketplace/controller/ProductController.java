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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        // Retrieve the current authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Product product = productService.findById(id);
        if (product == null) {
            // Handle the case where the product does not exist
            redirectAttributes.addFlashAttribute("errorMessage", "Product not found.");
            return "redirect:/products/my";
        }

        if (!product.getUser().getId().equals(user.getId())) {
            // Handle unauthorized deletion attempt
            redirectAttributes.addFlashAttribute("errorMessage", "You are not authorized to delete this product.");
            return "redirect:/products/my";
        }

        productService.deleteProductById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Product deleted successfully.");

        return "redirect:/products/my";
    }

    @GetMapping("/edit/{id}")
    public String showEditProductForm(@PathVariable Long id, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Product product = productService.findById(id);
        if (product == null) {
            return "redirect:/products/my?error=ProductNotFound";
        }

        if (!product.getUser().getId().equals(user.getId())) {
            return "redirect:/products/my?error=Unauthorized";
        }

        model.addAttribute("product", product);
        return "edit_product";
    }

    @PostMapping("/edit/{id}")
    public String editProduct(@PathVariable Long id,
                              @ModelAttribute("product") Product updatedProduct,
                              Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Product existingProduct = productService.findById(id);
        if (existingProduct == null) {
            return "redirect:/products/my?error=ProductNotFound";
        }

        if (!existingProduct.getUser().getId().equals(user.getId())) {
            return "redirect:/products/my?error=Unauthorized";
        }

        existingProduct.setName(updatedProduct.getName());
        existingProduct.setDescription(updatedProduct.getDescription());
        existingProduct.setPrice(updatedProduct.getPrice());
        existingProduct.setCategory(updatedProduct.getCategory());
        existingProduct.setImagePath(updatedProduct.getImagePath());
        existingProduct.setQuantityAvailable(updatedProduct.getQuantityAvailable());


        productService.updateProduct(existingProduct);

        return "redirect:/products/my?success=ProductUpdated";
    }


}
