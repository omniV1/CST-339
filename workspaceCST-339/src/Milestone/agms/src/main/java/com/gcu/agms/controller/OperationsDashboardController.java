package com.gcu.agms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.gcu.agms.service.GateOperationsService;

@Controller
@RequestMapping("/operations")
public class OperationsDashboardController {
    
    // Inject our gate operations service
    @Autowired
    private GateOperationsService gateOperationsService;
    
    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        // Add the page title for our view
        model.addAttribute("pageTitle", "Operations Dashboard - AGMS");
        
        // Add gate status data and statistics to our model
        // These method names now match exactly with what's in our service
        model.addAttribute("gateStatuses", gateOperationsService.getAllGateStatuses());
        model.addAttribute("statistics", gateOperationsService.getStatistics());
        
        return "dashboard/operations";
    }
}