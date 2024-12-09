package com.makers.marketplace.controller;

import com.makers.marketplace.model.User;
import com.makers.marketplace.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {
    @Autowired
    private UserService userService;

    @GetMapping("/signup")
    public String showSignupForm(Model model) {
        model.addAttribute("user", new User());
        return "signup";
    }

    @PostMapping("/signup")
    public String registerUser(@ModelAttribute("user") User user, 
                                BindingResult result, 
                                Model model) {
        try {
            userService.registerNewUser(user);
            return "redirect:/login?success";
        } catch (RuntimeException e) {
            result.rejectValue("username", "error.user", "Username already exists");
            return "signup";
        }
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}