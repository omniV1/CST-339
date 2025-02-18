package com.gcu.agms.controller.core;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * AboutController is a Spring MVC controller that handles HTTP GET requests
 * for the "/about" URL. It returns the name of the view that displays the 
 * about page.
 * 
 * Annotations:
 * @GetMapping("/about") - Maps HTTP GET requests to the about() method.
 * 
 * Methods:
 * - about(): Returns the name of the view for the about page.
 * - contact(): Returns the name of the view for the contact page.
 */
@Controller
public class AboutController {
    
    /**
     * Handles requests to the about page
     * @return The name of the view template to render
     */
    @GetMapping("/about")
    public String about() {
        return "about";
    }

    /**
     * Handles requests to the contact page
     * @return The name of the view template to render
     */
    @GetMapping("/contact")
    public String contact() {
        return "contact";
    }
}
