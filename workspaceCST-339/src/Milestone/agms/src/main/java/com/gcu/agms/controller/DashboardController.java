package com.gcu.agms.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;

@Controller
public class DashboardController {
    
    @GetMapping("/dashboard")
    public String showDashboard(Model model, HttpSession session) {
        // Get the user's role from the session
        String userRole = (String) session.getAttribute("userRole");
        
        // Using modern switch expression for cleaner role-based routing
        return userRole == null ? "redirect:/login" : switch(userRole) {
            case "ADMIN" -> "redirect:/admin/dashboard";
            case "OPERATIONS_MANAGER" -> "redirect:/operations/dashboard";
            case "GATE_MANAGER" -> "redirect:/gates/dashboard";
            case "AIRLINE_STAFF" -> "redirect:/airline/dashboard";
            default -> "redirect:/";
        };
    }
}