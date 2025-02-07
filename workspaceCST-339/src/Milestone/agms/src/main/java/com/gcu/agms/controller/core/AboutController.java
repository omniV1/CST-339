package com.gcu.agms.controller.core;

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
 */
public class AboutController {
                
    
    /** 
     * @return String
     */
    // This is a controller for the about page
    // It is a simple controller that returns the about page
    // It is mapped to the /about URL
    // It is a simple controller that returns the about page
    // It is mapped to the /about URL
    @GetMapping("/about")
    public String about() {
        return "about";
    }
    
}
