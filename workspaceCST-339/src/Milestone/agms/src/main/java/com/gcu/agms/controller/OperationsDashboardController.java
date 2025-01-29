package com.gcu.agms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.gcu.agms.service.GateOperationsService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/operations")
public class OperationsDashboardController {
    
    @Autowired
    private GateOperationsService gateOperationsService;
    
    @GetMapping("/dashboard")
    public String showDashboard(Model model, HttpSession session) {
        // Verify user role
        String userRole = (String) session.getAttribute("userRole");
        if (!"OPERATIONS_MANAGER".equals(userRole)) {
            return "redirect:/login";
        }
        
        // Add the page title
        model.addAttribute("pageTitle", "Operations Dashboard - AGMS");
        
        // Add gate status data and statistics
        model.addAttribute("gateStatuses", gateOperationsService.getAllGateStatuses());
        model.addAttribute("statistics", gateOperationsService.getStatistics());
        
        // Return the dashboard view
        return "dashboard/operations";
    }
}