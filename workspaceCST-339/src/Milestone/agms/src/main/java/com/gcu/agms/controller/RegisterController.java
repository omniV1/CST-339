package com.gcu.agms.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.gcu.agms.model.UserModel;

import jakarta.validation.Valid;

@Controller
public class RegisterController {
    
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
            @Valid @ModelAttribute("userModel") UserModel userModel,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        
        // If there are validation errors, return to the registration form
        if (bindingResult.hasErrors()) {
            return "register";
        }

        // For now, just redirect to login page with success message
        // In Milestone 4, this will include database persistence
        redirectAttributes.addFlashAttribute("successMessage", 
            "Registration successful! Please login with your credentials.");
        return "redirect:/login";
    }
}