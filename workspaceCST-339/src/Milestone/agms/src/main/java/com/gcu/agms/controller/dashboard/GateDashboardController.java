package com.gcu.agms.controller.dashboard;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.gcu.agms.model.gate.AssignmentModel;
import com.gcu.agms.model.gate.GateModel;
import com.gcu.agms.service.flight.AssignmentService;
import com.gcu.agms.service.gate.GateManagementService;
import com.gcu.agms.service.gate.GateOperationsService;

import jakarta.servlet.http.HttpSession;

/**
 * Controller responsible for gate-level management operations.
 * This controller handles the specific needs of Gate Managers, focusing on
 * maintenance scheduling, status updates, and issue reporting. According to
 * the system design, Gate Managers have limited permissions focused on
 * day-to-day gate operations rather than system-wide management.
 */
@Controller
@RequestMapping("/gates")
public class GateDashboardController {
    private static final Logger logger = LoggerFactory.getLogger(GateDashboardController.class);
    
    // Add constants for view paths and redirects
    private static final String DASHBOARD_REDIRECT = "redirect:/gates/dashboard";
    private static final String PAGE_TITLE_ATTR = "pageTitle";
    private static final String SUCCESS_ATTR = "success";
    private static final String ERROR_ATTR = "error";
    
    private final GateOperationsService gateOperationsService;
    private final GateManagementService gateManagementService;
    private final AssignmentService assignmentService;
    
    /**
     * Constructor injection of required services.
     * Gate Managers need access to both operational status and basic gate
     * management capabilities, but with more limited scope than admins or
     * operations managers.
     */
    public GateDashboardController(
            GateOperationsService gateOperationsService,
            GateManagementService gateManagementService,
            AssignmentService assignmentService) {
        this.gateOperationsService = gateOperationsService;
        this.gateManagementService = gateManagementService;
        this.assignmentService = assignmentService;
        
        logger.info("GateDashboardController initialized with services: {}, {}, {}", 
            gateOperationsService.getClass().getSimpleName(),
            gateManagementService.getClass().getSimpleName(),
            assignmentService.getClass().getSimpleName());
    }
    
    /**
     * Displays the gate manager's dashboard showing gate statuses and maintenance information.
     * This view focuses on the day-to-day operational aspects of gate management rather
     * than system-wide configuration.
     */
    @GetMapping("/dashboard")
    public String showDashboard(Model model, HttpSession session) {
        // Verify gate manager role
        String userRole = (String) session.getAttribute("userRole");
        if (!"GATE_MANAGER".equals(userRole)) {
            logger.warn("Unauthorized access attempt to gate manager dashboard");
            return "redirect:/login";
        }
        
        logger.info("Loading gate manager dashboard");
        
        // Add page title
        model.addAttribute(PAGE_TITLE_ATTR, "Gate Management Dashboard - AGMS");
        
        // Get current gate statuses and statistics
        Map<String, GateOperationsService.GateStatus> gateStatuses = gateOperationsService.getAllGateStatuses();
        logger.debug("Retrieved {} gate statuses", gateStatuses.size());
        model.addAttribute("gateStatuses", gateStatuses);
        
        Map<String, Integer> statistics = gateOperationsService.getStatistics();
        logger.debug("Retrieved statistics: {}", statistics);
        model.addAttribute("statistics", statistics);
        
        // Get gates and their assignments
        List<GateModel> gates = gateManagementService.getAllGates();
        logger.debug("Retrieved {} gates", gates.size());
        model.addAttribute("gates", gates);
        
        // Add assignments for each gate
        Map<String, List<AssignmentModel>> gateAssignments = new HashMap<>();
        for (GateModel gate : gates) {
            List<AssignmentModel> assignments = assignmentService.getAssignmentsForGate(gate.getGateId());
            logger.debug("Gate {} has {} assignments", gate.getGateId(), assignments.size());
            gateAssignments.put(gate.getGateId(), assignments);
        }
        model.addAttribute("gateAssignments", gateAssignments);
        
        logger.info("Gate manager dashboard loaded successfully");
        return "dashboard/gate";
    }
    
    /**
     * Displays detailed information about a specific gate.
     * Gate managers can view comprehensive information about individual gates,
     * including maintenance history and current status.
     */
    @GetMapping("/details/{gateId}")
    public String showGateDetails(@PathVariable String gateId, Model model) {
        logger.info("Accessing details for gate: {}", gateId);
        
        Optional<GateModel> gate = gateManagementService.getGateById(gateId);
        if (gate.isPresent()) {
            model.addAttribute("gate", gate.get());
            model.addAttribute("currentStatus", 
                gateOperationsService.getAllGateStatuses().get(gateId));
            return "gates/details";
        }
        
        return DASHBOARD_REDIRECT;
    }
    
    /**
     * Updates the operational status of a gate.
     * Gate managers can change gate status to reflect current operational conditions,
     * such as marking a gate for maintenance or returning it to service.
     */
    @PostMapping("/status/{gateId}")
    public String updateGateStatus(
            @PathVariable String gateId,
            @RequestParam GateOperationsService.GateStatus newStatus,
            RedirectAttributes redirectAttributes) {
        logger.info("Updating status for gate {} to {}", gateId, newStatus);
        
        // Note: In a real implementation, this would call a method to update the status
        // For now, we'll just show the concept with a success message
        
        redirectAttributes.addFlashAttribute(SUCCESS_ATTR, 
            "Gate " + gateId + " status updated to " + newStatus.getLabel());
        return "redirect:/gates/details/" + gateId;
    }
    
    /**
     * Displays the maintenance scheduling interface for a specific gate.
     * Allows gate managers to schedule and track maintenance activities.
     */
    @GetMapping("/maintenance/{gateId}")
    public String showMaintenanceSchedule(@PathVariable String gateId, Model model) {
        logger.info("Accessing maintenance schedule for gate: {}", gateId);
        
        Optional<GateModel> gate = gateManagementService.getGateById(gateId);
        if (gate.isPresent()) {
            model.addAttribute("gate", gate.get());
            // In a real implementation, we would also load maintenance history
            // and scheduled maintenance records
            return "gates/maintenance";
        }
        
        return DASHBOARD_REDIRECT;
    }
    
    /**
     * Reports an issue with a specific gate.
     * Allows gate managers to document and report problems that require attention.
     */
    @PostMapping("/report-issue/{gateId}")
    public String reportIssue(
            @PathVariable String gateId,
            @RequestParam String issueDescription,
            RedirectAttributes redirectAttributes) {
        logger.info("Reporting issue for gate {}: {}", gateId, issueDescription);
        
        // Note: In a real implementation, this would create an issue record
        // For now, we'll just show the concept with a success message
        
        redirectAttributes.addFlashAttribute(SUCCESS_ATTR, 
            "Issue reported for gate " + gateId);
        return "redirect:/gates/details/" + gateId;
    }

    @PostMapping("/assignments/create")
    public String createAssignment(@ModelAttribute AssignmentModel assignment,
                                   RedirectAttributes redirectAttributes) {
        logger.info("Creating new assignment for gate: {}", assignment.getGateId());
        
        // Set the Admin username explicitly
        assignment.setAssignedBy("admin");  // Make sure it's exactly "Admin" with capital A
        assignment.setCreatedBy("admin");   // Case matters in MySQL foreign keys
        
        // Initialize timestamps if needed
        if (assignment.getCreatedAt() == null) {
            assignment.initializeTimestamps();
        }
        
        boolean created = assignmentService.createAssignment(assignment);
        if (created) {
            redirectAttributes.addFlashAttribute("success", 
                "Assignment created successfully");
        } else {
            redirectAttributes.addFlashAttribute("error", 
                "Failed to create assignment - time conflict");
        }
        
        return "redirect:/gates/dashboard";
    }

    @PostMapping("/assignments/delete/{id}")
    public String deleteAssignment(@PathVariable Long id, 
                                 @RequestParam String gateId,
                                 RedirectAttributes redirectAttributes) {
        logger.info("Deleting assignment {} from gate {}", id, gateId);
        
        boolean deleted = assignmentService.deleteAssignment(gateId, id);
        if (deleted) {
            redirectAttributes.addFlashAttribute(SUCCESS_ATTR, 
                "Assignment deleted successfully");
        } else {
            redirectAttributes.addFlashAttribute(ERROR_ATTR, 
                "Failed to delete assignment");
        }
        
        return DASHBOARD_REDIRECT;
    }

    @GetMapping("/assignments/print")
    public ResponseEntity<Resource> printSchedule() {
        logger.info("Generating gate schedule printout");
        
        String content = generateScheduleContent();
        ByteArrayResource resource = new ByteArrayResource(content.getBytes());
        
        return ResponseEntity.ok()
            .contentType(MediaType.TEXT_PLAIN)
            .header(HttpHeaders.CONTENT_DISPOSITION, 
                "attachment; filename=gate-schedule.txt")
            .body(resource);
    }

    private String generateScheduleContent() {
        StringBuilder content = new StringBuilder();
        content.append("Gate Schedule Report\n");
        content.append("Generated: ").append(LocalDateTime.now()).append("\n\n");
        
        List<GateModel> gates = gateManagementService.getAllGates();
        for (GateModel gate : gates) {
            content.append("Gate: ").append(gate.getGateId()).append("\n");
            List<AssignmentModel> assignments = assignmentService.getAssignmentsForGate(gate.getGateId());
            for (AssignmentModel assignment : assignments) {
                content.append(String.format("  %s: %s - %s\n", 
                    assignment.getFlightNumber(), 
                    assignment.getStartTime().format(DateTimeFormatter.ISO_LOCAL_TIME),
                    assignment.getEndTime().format(DateTimeFormatter.ISO_LOCAL_TIME)));
            }
            content.append("\n");
        }
        
        return content.toString();
    }

    @GetMapping("/details/{gateId}/schedule")
    public String showSchedule(@PathVariable String gateId, Model model) {
        logger.info("Viewing schedule for gate: {}", gateId);

        Optional<GateModel> gate = gateManagementService.getGateById(gateId);
        if (gate.isPresent()) {
            model.addAttribute(PAGE_TITLE_ATTR, "Gate Schedule - AGMS");
            model.addAttribute("gate", gate.get());
            model.addAttribute("assignments", assignmentService.getAssignmentsForGate(gateId));
            return "gates/schedule";
        }
        
        return DASHBOARD_REDIRECT;
    }
}