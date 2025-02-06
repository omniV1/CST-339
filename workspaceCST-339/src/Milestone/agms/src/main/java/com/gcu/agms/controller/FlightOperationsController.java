package com.gcu.agms.controller;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gcu.agms.model.AssignmentModel;
import com.gcu.agms.service.AssignmentService;
import com.gcu.agms.service.FlightOperationsService;
import com.gcu.agms.service.GateManagementService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
/**
 * Controller handling flight operations functionality.
 * This controller demonstrates proper use of Spring MVC patterns and
 * dependency injection while managing flight operations.
 */
@Controller
@RequestMapping("/operations")
public class FlightOperationsController {
    private static final Logger logger = LoggerFactory.getLogger(FlightOperationsController.class);
    
    private final FlightOperationsService flightOperationsService;
    private final AssignmentService assignmentService;
    private final GateManagementService gateManagementService;
    
    public FlightOperationsController(FlightOperationsService flightOperationsService,
                                    AssignmentService assignmentService,
                                    GateManagementService gateManagementService) {
        this.flightOperationsService = flightOperationsService;
        this.assignmentService = assignmentService;
        this.gateManagementService = gateManagementService;
    }
    
    @GetMapping("/dashboard")
public String showDashboard(Model model, HttpSession session) {
    String userRole = (String) session.getAttribute("userRole"); 
    if (!"OPERATIONS_MANAGER".equals(userRole)) {
        return "redirect:/login";
    }
    
    Map<String, Object> gateUtilization = flightOperationsService.getGateUtilization();
    List<Map<String, Object>> gateStatusList = (List<Map<String, Object>>) gateUtilization.get("gateStatuses");
    
    // Add assignments data for each gate status
    for (Map<String, Object> gateStatus : gateStatusList) {
        String gateId = (String) gateStatus.get("gate");
        Map<String, AssignmentModel> assignments = 
            assignmentService.getCurrentAndNextAssignments(gateId);
        gateStatus.put("assignments", assignments);
    }

    model.addAttribute("pageTitle", "Flight Operations Dashboard - AGMS");
    model.addAttribute("gateUtilization", gateUtilization);
    model.addAttribute("statistics", flightOperationsService.getOperationalStatistics());
    
    return "dashboard/operations";
}
    
    // Add endpoints for assignment management
    @PostMapping("/assignments/create")
    @ResponseBody
    public String createAssignment(@Valid @RequestBody AssignmentModel assignment) {
        boolean success = assignmentService.createAssignment(assignment);
        return success ? "Assignment created successfully" 
                      : "Failed to create assignment";
    }
    
    @PostMapping("/assignments/update")
    @ResponseBody
    public String updateAssignment(@RequestParam String gateId,
                                 @RequestParam Long assignmentId,
                                 @Valid @RequestBody AssignmentModel assignment) {
        boolean success = assignmentService.updateAssignment(gateId, assignmentId, assignment);
        return success ? "Assignment updated successfully" 
                      : "Failed to update assignment";
    }
    
    @DeleteMapping("/assignments/delete")
    @ResponseBody
    public String deleteAssignment(@RequestParam String gateId,
                                 @RequestParam Long assignmentId) {
        boolean success = assignmentService.deleteAssignment(gateId, assignmentId);
        return success ? "Assignment deleted successfully" 
                      : "Failed to delete assignment";
    }
}