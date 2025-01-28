package com.gcu.agms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.gcu.agms.model.UserModel;
import com.gcu.agms.service.UserService;

import jakarta.validation.Valid;

@Controller
public class RegisterController {

    @Autowired
    private UserService userService; // Inject UserService

    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        // Add a new UserModel to the model if it doesn't exist
        if (!model.containsAttribute("userModel")) {
            model.addAttribute("userModel", new UserModel());
        }
        model.addAttribute("pageTitle", "Register - AGMS");
        return "register";
    }

    @PostMapping("/doRegister")
    public String processRegistration(
            @Valid @ModelAttribute("serModel") UserModel userModel,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        // If there are validation errors, return to the registration form
        if (bindingResult.hasErrors()) {
            return "register";
        }

        // Check if the username already exists
        if (userService.findByUsername(userModel.getUsername()).isPresent()) {
            bindingResult.rejectValue("username", "error.userModel", "Username already exists");
            return "register"; // Return to the registration page with error
        }

        // Register the new user
        userService.registerUser(userModel);
        System.out.println("User registered: " + userModel.getUsername());

        // Redirect to login page with success message
        redirectAttributes.addFlashAttribute("successMessage", 
            "Registration successful! Please login with your credentials.");
        return "redirect:/login";
    }
}
