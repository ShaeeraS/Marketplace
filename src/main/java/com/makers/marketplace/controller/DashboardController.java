package com.makers.marketplace.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping("/")
    public String homeRedirect() {
        return "redirect:/dashboard"; // Redirect to dashboard
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // Get the logged-in user's username
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        model.addAttribute("username", username);
        return "dashboard"; // Render the dashboard view
    }

    @GetMapping("/profile")
    public String viewProfile(Model model) {
        // Get the logged-in user's details
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        // Add user details to the model for the profile page
        model.addAttribute("username", username);

        return "profile"; // Render the 'profile.html' Thymeleaf template
    }
}
