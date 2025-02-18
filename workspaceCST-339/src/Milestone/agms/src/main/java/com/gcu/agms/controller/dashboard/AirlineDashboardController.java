package com.gcu.agms.controller.dashboard;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.gcu.agms.service.gate.GateManagementService;
import com.gcu.agms.service.gate.GateOperationsService;

import jakarta.servlet.http.HttpSession;

/**
 * Controller for the airline staff dashboard functionality.
 * This controller provides airline staff with the ability to view gate assignments,
 * request changes, and monitor flight operations. According to the system design,
 * airline staff have read access to gate information and can request changes,
 * but cannot directly modify gate assignments.
 */
@Controller
@RequestMapping("/airline")
public class AirlineDashboardController {
    private static final Logger logger = LoggerFactory.getLogger(AirlineDashboardController.class);
    
    private final GateOperationsService gateOperationsService;
    private final GateManagementService gateManagementService;
    
    /**
     * Constructor injection of required services.
     * Airline staff primarily need read access to gate information and
     * the ability to submit change requests. They don't need the full
     * management capabilities of other roles.
     */
    public AirlineDashboardController(
            GateOperationsService gateOperationsService,
            GateManagementService gateManagementService) {
        this.gateOperationsService = gateOperationsService;
        this.gateManagementService = gateManagementService;
    }
    
    /**
     * Displays the airline staff dashboard showing flight schedules and gate assignments.
     * This view focuses on information relevant to airline operations rather than
     * system-wide gate management.
     */
    @GetMapping("/dashboard")
    public String showDashboard(Model model, HttpSession session) {
        // Verify airline staff role
        String userRole = (String) session.getAttribute("userRole");
        if (!"AIRLINE_STAFF".equals(userRole)) {
            logger.warn("Unauthorized access attempt to airline staff dashboard");
            return "redirect:/login";
        }
        
        logger.info("Loading airline staff dashboard");
        
        // Add page title and basic gate information
        model.addAttribute("pageTitle", "Airline Staff Dashboard - AGMS");
        model.addAttribute("gateStatuses", gateOperationsService.getAllGateStatuses());
        
        // Get today's flight schedule information
        // Note: In a real implementation, this would filter for the specific airline
        model.addAttribute("gates", gateManagementService.getAllGates());
        
        // Add performance metrics
        model.addAttribute("statistics", gateOperationsService.getStatistics());
        
        logger.info("Airline staff dashboard loaded successfully");
        return "dashboard/airline";
    }
    
    /**
     * Displays flight schedule information for a specific gate.
     * Allows airline staff to view detailed scheduling information for their flights.
     */
    @GetMapping("/schedule/{gateId}")
    public String viewGateSchedule(@PathVariable String gateId, Model model) {
        logger.info("Viewing schedule for gate: {}", gateId);
        
        model.addAttribute("pageTitle", "Gate Schedule - AGMS");
        model.addAttribute("gate", gateManagementService.getGateById(gateId));
        model.addAttribute("status", gateOperationsService.getAllGateStatuses().get(gateId));
        
        // In a real implementation, we would also load flight schedule data
        // specific to the airline and gate
        
        return "airline/schedule";
    }
    
    /**
     * Submits a gate change request for a flight.
     * Allows airline staff to request gate reassignments when needed.
     */
    @PostMapping("/request-change")
    public String requestGateChange(
            @RequestParam String currentGateId,
            @RequestParam String requestedGateId,
            @RequestParam String reason,
            RedirectAttributes redirectAttributes) {
        logger.info("Gate change request from {} to {} - Reason: {}", 
            currentGateId, requestedGateId, reason);
        
        // In a real implementation, this would create a change request record
        // that operations staff would need to approve
        
        redirectAttributes.addFlashAttribute("success", 
            "Gate change request submitted successfully");
        return "redirect:/airline/schedule/" + currentGateId;
    }
    
    /**
     * Views operational alerts and notifications.
     * Provides airline staff with updates about gate and flight operations.
     */
    @GetMapping("/alerts")
    public String viewAlerts(Model model) {
        logger.info("Accessing airline staff alerts");
        
        model.addAttribute("pageTitle", "Operational Alerts - AGMS");
        
        // In a real implementation, this would load airline-specific
        // alerts and notifications
        
        return "airline/alerts";
    }
    
    /**
     * Displays performance metrics for gates used by the airline.
     * Provides insights into gate utilization and on-time performance.
     */
    @GetMapping("/performance")
    public String viewPerformance(Model model) {
        logger.info("Accessing airline performance metrics");
        
        model.addAttribute("pageTitle", "Gate Performance - AGMS");
        model.addAttribute("statistics", gateOperationsService.getStatistics());
        
        // In a real implementation, this would calculate and display
        // airline-specific performance metrics
        
        return "airline/performance";
    }
}