package com.gcu.agms.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Main controller for handling homepage and general navigation
 * This controller manages the root URI and main application pages
 */
@Controller
public class HomeController {
    
    /**
     * Handles requests to the root URI and displays the main homepage
     * @param model The Spring MVC model for passing data to the view
     * @return The name of the view template to render
     */
    @GetMapping("/")
    public String showHomePage(Model model) {
        // Add page title to the model
        model.addAttribute("pageTitle", "AGMS - Airport Gate Management System");
        // Add welcome message
        model.addAttribute("welcomeMessage", "Welcome to the Airport Gate Management System");
        return "home";
    }

    /**
     * Handles requests to the about page
     * @param model The Spring MVC model for passing data to the view
     * @return The name of the view template to render
     */
    @GetMapping("/about")
    public String showAboutPage(Model model) {
        model.addAttribute("pageTitle", "About AGMS");
        return "about";
    }
}