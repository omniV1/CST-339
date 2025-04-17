package com.gcu.agms.controller.dashboard;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gcu.agms.model.flight.AircraftModel;
import com.gcu.agms.model.flight.FlightModel;
import com.gcu.agms.model.gate.AssignmentModel;
import com.gcu.agms.model.gate.AssignmentStatus;
import com.gcu.agms.model.maintenance.MaintenanceRecord;
import com.gcu.agms.service.flight.AssignmentService;
import com.gcu.agms.service.flight.FlightOperationsService;
import com.gcu.agms.service.maintenance.MaintenanceRecordService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

/**
 * Flight Operations Controller for the Airport Gate Management System.
 * 
 * This controller manages all aspects of flight operations within the AGMS application
 * and is accessible only to users with the OPERATIONS_MANAGER role. It provides a
 * comprehensive interface for managing:
 * 
 * 1. Flight Management
 *    - Creating, updating, and deleting flights
 *    - Tracking flight status changes (scheduled, boarding, departed, etc.)
 *    - Viewing flight details and operational statistics
 * 
 * 2. Aircraft Management
 *    - Tracking aircraft status and location
 *    - Managing aircraft maintenance scheduling
 *    - Viewing aircraft details and maintenance history
 * 
 * 3. Gate Assignment Management
 *    - Creating and managing gate assignments for flights
 *    - Resolving gate conflicts
 *    - Monitoring gate utilization
 * 
 * The controller follows a RESTful API design, with traditional web endpoints for UI pages
 * and AJAX endpoints for real-time data updates. It implements both MVC pattern (for view-based
 * endpoints) and REST API pattern (for AJAX/JSON endpoints) within the same controller.
 * 
 * This is one of the role-specific dashboard controllers in the system, with access
 * restricted by Spring Security configuration to users with OPERATIONS_MANAGER role.
 */
@Controller
@RequestMapping("/operations")
@Tag(name = "Flight Operations", description = "Endpoints for managing flight operations, aircraft, and gate assignments")
@SecurityRequirement(name = "bearerAuth")
public class FlightOperationsController {
    /**
     * Logger for this controller class.
     * Used to record operations activities for debugging, auditing, and troubleshooting.
     */
    private static final Logger logger = LoggerFactory.getLogger(FlightOperationsController.class);
    
    /**
     * Response attribute constants.
     * These constants are used as keys in response maps to ensure consistency
     * across all controller methods that return JSON responses.
     */
    private static final String SUCCESS_KEY = "success";
    private static final String MESSAGE_KEY = "message";
    private static final String FLIGHT_NUMBER_KEY = "flightNumber";
    private static final String AIRCRAFT_KEY = "aircraft";
    private static final String STATISTICS_KEY = "statistics";
    private static final String ACTIVE_FLIGHTS_KEY = "activeFlights";
    private static final String AVAILABLE_AIRCRAFT_KEY = "availableAircraft";
    private static final String ASSIGNMENTS_KEY = "assignments";
    
    /**
     * Service dependencies injected through constructor.
     * These services provide the business logic for flight operations.
     */
    private final FlightOperationsService flightOperationsService;
    private final AssignmentService assignmentService;
    private final MaintenanceRecordService maintenanceRecordService;

    /**
     * Constructor injection of required services.
     * 
     * This controller requires three key services to function:
     * - FlightOperationsService: Core service for flight and aircraft management
     * - AssignmentService: Handles gate assignment operations and conflict resolution
     * - MaintenanceRecordService: Manages aircraft maintenance scheduling and tracking
     * 
     * Constructor injection is used to ensure all required dependencies are available
     * when the controller is initialized and to support immutability (final fields).
     * 
     * @param flightOperationsService Service handling flight and aircraft operations
     * @param assignmentService Service handling gate assignments for flights
     * @param maintenanceRecordService Service handling aircraft maintenance records
     */
    public FlightOperationsController(
            FlightOperationsService flightOperationsService,
            AssignmentService assignmentService,
            MaintenanceRecordService maintenanceRecordService) {
        this.flightOperationsService = flightOperationsService;
        this.assignmentService = assignmentService;
        this.maintenanceRecordService = maintenanceRecordService;
        logger.info("Initialized FlightOperationsController with services");
    }

    @Operation(
        summary = "Get operations dashboard view",
        description = "Displays the main operations dashboard with active flights, statistics, and aircraft status"
    )
    @GetMapping("/dashboard")
    public String showDashboard(Model model, HttpSession session) {
        logger.info("Loading operations dashboard view");
        
        // Add real-time operational data to the model
        model.addAttribute(ACTIVE_FLIGHTS_KEY, flightOperationsService.getActiveFlights());
        model.addAttribute(STATISTICS_KEY, flightOperationsService.getOperationalStatistics());
        model.addAttribute(AIRCRAFT_KEY, flightOperationsService.getAllAircraft());
        model.addAttribute(AVAILABLE_AIRCRAFT_KEY, flightOperationsService.getAvailableAircraft());
        model.addAttribute("pageTitle", "Flight Operations Dashboard - AGMS");

        logger.debug("Dashboard data loaded: {} active flights, {} total aircraft", 
            flightOperationsService.getActiveFlights().size(),
            flightOperationsService.getAllAircraft().size());
            
        return "dashboard/operations";
    }

    @Operation(
        summary = "Get real-time dashboard data",
        description = "Provides real-time updates for the dashboard including operational statistics, active flights, and aircraft status"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved dashboard data"),
        @ApiResponse(responseCode = "403", description = "Access denied - Insufficient permissions")
    })
    @GetMapping("/dashboard/data")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getDashboardData() {
        logger.debug("Fetching real-time dashboard data for AJAX update");
        
        Map<String, Object> dashboardData = new HashMap<>();
        
        // Compile all required dashboard data into a single response
        dashboardData.put(STATISTICS_KEY, flightOperationsService.getOperationalStatistics());
        dashboardData.put(ACTIVE_FLIGHTS_KEY, flightOperationsService.getActiveFlights());
        dashboardData.put(AIRCRAFT_KEY, flightOperationsService.getAllAircraft());
        
        return ResponseEntity.ok(dashboardData);
    }

    @Operation(
        summary = "Update aircraft status",
        description = "Updates the operational status and location of an aircraft"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Aircraft status updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
        @ApiResponse(responseCode = "403", description = "Access denied - Insufficient permissions")
    })
    @PostMapping("/aircraft/update")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateAircraftStatus(
            @Parameter(description = "Aircraft registration number", required = true)
            @RequestParam String registrationNumber,
            @Parameter(description = "New operational status", required = true)
            @RequestParam AircraftModel.AircraftStatus status,
            @Parameter(description = "Current physical location of the aircraft", required = true)
            @RequestParam String location) {
        
        logger.info("Updating aircraft status - Registration: {}, New Status: {}, Location: {}", 
            registrationNumber, status, location);
        
        // Attempt to update the aircraft status through the service
        boolean updated = flightOperationsService.updateAircraftStatus(
            registrationNumber, status, location);
        
        // Create appropriate response based on the update result
        if (updated) {
            logger.info("Successfully updated status for aircraft: {}", registrationNumber);
        } else {
            logger.warn("Failed to update status for aircraft: {}", registrationNumber);
        }
            
        return createResponse(updated, "Aircraft status updated successfully", 
                            "Failed to update aircraft status");
    }

    @Operation(
        summary = "Create new flight",
        description = "Creates a new flight in the system with the provided details"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Flight created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid flight data"),
        @ApiResponse(responseCode = "403", description = "Access denied - Insufficient permissions")
    })
    @PostMapping("/flights/create")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createFlight(
            @Parameter(description = "Flight details", required = true)
            @RequestBody @Valid FlightModel flight) {
        logger.info("Received request to create new flight with details: flightNumber={}, airlineCode={}, origin={}, destination={}, assignedAircraft={}", 
            flight.getFlightNumber(),
            flight.getAirlineCode(),
            flight.getOrigin(),
            flight.getDestination(),
            flight.getAssignedAircraft()
        );
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Validate essential flight identification data
            // Flight number and airline code are required to create a unique identifier
            if (flight.getFlightNumber() == null || flight.getAirlineCode() == null) {
                logger.warn("Invalid flight data: missing required fields");
                response.put(SUCCESS_KEY, false);
                response.put(MESSAGE_KEY, "Missing required flight information");
                return ResponseEntity.badRequest().body(response);
            }

            // Attempt to create or update the flight through the service layer
            boolean created = flightOperationsService.updateFlight(flight);
            
            if (created) {
                // Log success and prepare response with flight details
                logger.info("Successfully created flight: {}", flight.getFlightNumber());
                
                // Get updated active flights to verify the creation
                List<Map<String, Object>> activeFlights = flightOperationsService.getActiveFlights();
                logger.info("Current active flights count: {}", activeFlights.size());
                
                // Build success response with created flight information
                response.put(SUCCESS_KEY, true);
                response.put(MESSAGE_KEY, "Flight created successfully");
                response.put(FLIGHT_NUMBER_KEY, flight.getFlightNumber());
                return ResponseEntity.ok(response);
            } else {
                // Log failure and prepare error response
                logger.warn("Failed to create flight: {}", flight.getFlightNumber());
                response.put(SUCCESS_KEY, false);
                response.put(MESSAGE_KEY, "Failed to create flight");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            // Handle any unexpected exceptions during flight creation
            logger.error("Error creating flight: {} - {}", flight.getFlightNumber(), e.getMessage(), e);
            return createErrorResponse("Error creating flight: " + e.getMessage());
        }
    }

    @Operation(
        summary = "Update flight status",
        description = "Updates the operational status of a flight and optionally its location"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Flight status updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid status or flight not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - Insufficient permissions")
    })
    @PostMapping("/flights/status")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateFlightStatus(
            @Parameter(description = "Flight number", required = true)
            @RequestParam String flightNumber,
            @Parameter(description = "New flight status", required = true)
            @RequestParam String status,
            @Parameter(description = "Current location of the flight", required = false)
            @RequestParam(required = false) String location) {
        
        logger.info("Updating status for flight {} to {}", flightNumber, status);
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Update the flight status using the service
            // The service layer handles validation of status transitions
            // and any required business logic for the status change
            boolean updated = flightOperationsService.updateFlightStatus(flightNumber, status, location);
            
            if (updated) {
                // Status update successful
                logger.info("Successfully updated flight {} status to {}", flightNumber, status);
                response.put(SUCCESS_KEY, true);
                response.put(MESSAGE_KEY, "Flight status updated successfully");
            } else {
                // Status update failed
                logger.warn("Failed to update flight {} status to {}", flightNumber, status);
                response.put(SUCCESS_KEY, false);
                response.put(MESSAGE_KEY, "Failed to update flight status");
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            // Handle any exceptions during status update
            logger.error("Error updating flight status: {}", e.getMessage());
            return createErrorResponse("Error updating flight status: " + e.getMessage());
        }
    }

    @Operation(
        summary = "Update flight details",
        description = "Updates multiple attributes of an existing flight"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Flight updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid flight data"),
        @ApiResponse(responseCode = "403", description = "Access denied - Insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Flight not found")
    })
    @PutMapping("/flights/update")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateFlight(
            @Parameter(description = "Updated flight information", required = true)
            @RequestBody @Valid FlightModel flight) {
        logger.info("Received request to update flight: {}", flight.getFlightNumber());
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Attempt to update all flight details through the service
            boolean updated = flightOperationsService.updateFlight(flight);
            
            // Prepare response based on update result
            response.put(SUCCESS_KEY, updated);
            response.put(MESSAGE_KEY, updated ? "Flight updated successfully" : "Failed to update flight");
            
            if (updated) {
                logger.info("Successfully updated flight: {}", flight.getFlightNumber());
            } else {
                logger.warn("Failed to update flight: {}", flight.getFlightNumber());
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Handle any exceptions during the update process
            logger.error("Error updating flight", e);
            return createErrorResponse("Error updating flight: " + e.getMessage());
        }
    }

    @Operation(
        summary = "Get flight details",
        description = "Retrieves detailed information about a specific flight"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Flight details retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied - Insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Flight not found")
    })
    @GetMapping("/flights/{flightNumber}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getFlightDetails(
            @Parameter(description = "Flight number", required = true)
            @PathVariable String flightNumber) {
        logger.info("Retrieving details for flight: {}", flightNumber);
        
        // Get comprehensive flight details from the service
        Map<String, Object> details = flightOperationsService.getFlightDetails(flightNumber);
        
        if (details.isEmpty()) {
            logger.warn("Flight not found: {}", flightNumber);
        } else {
            logger.debug("Retrieved details for flight: {}", flightNumber);
        }
        
        return ResponseEntity.ok(details);
    }

    @Operation(
        summary = "Delete flight",
        description = "Removes a flight from the system"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Flight deleted successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied - Insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Flight not found")
    })
    @DeleteMapping("/flights/{flightNumber}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteFlight(
            @Parameter(description = "Flight number", required = true)
            @PathVariable String flightNumber) {
        logger.info("Received request to delete flight: {}", flightNumber);
        Map<String, Object> response = new HashMap<>();
        
        try {
            boolean deleted = flightOperationsService.deleteFlight(flightNumber);
            response.put(SUCCESS_KEY, deleted);
            response.put(MESSAGE_KEY, deleted ? "Flight deleted successfully" : "Failed to delete flight");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error deleting flight", e);
            return createErrorResponse("Error deleting flight: " + e.getMessage());
        }
    }

    @Operation(
        summary = "Schedule aircraft maintenance",
        description = "Schedules maintenance for an aircraft at a specified date"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Maintenance scheduled successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
        @ApiResponse(responseCode = "403", description = "Access denied - Insufficient permissions")
    })
    @PostMapping("/aircraft/maintenance")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> scheduleMaintenance(
            @Parameter(description = "Aircraft registration number", required = true)
            @RequestParam String registrationNumber,
            @Parameter(description = "Scheduled maintenance date (format: yyyy-MM-dd HH:mm:ss)", required = true)
            @RequestParam String maintenanceDate,
            @Parameter(description = "Type of maintenance to perform", required = true)
            @RequestParam String maintenanceType,
            @Parameter(description = "Detailed description of maintenance work", required = true)
            @RequestParam String description) {
        
        logger.info("Scheduling maintenance - Registration: {}, Date: {}, Type: {}", 
            registrationNumber, maintenanceDate, maintenanceType);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Parse the date directly from the format sent by the client
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime parsedDate = LocalDateTime.parse(maintenanceDate, formatter);
            
            boolean scheduled = flightOperationsService.scheduleMaintenance(
                registrationNumber, 
                parsedDate,
                maintenanceType,
                description
            );
            
            response.put(SUCCESS_KEY, scheduled);
            response.put(MESSAGE_KEY, scheduled ? 
                "Maintenance scheduled successfully" : 
                "Failed to schedule maintenance");
            return ResponseEntity.ok(response);
        } catch (DateTimeParseException e) {
            logger.error("Error parsing maintenance date: {}", maintenanceDate, e);
            return createErrorResponse("Invalid date format. Please use format: YYYY-MM-DD HH:mm:ss");
        } catch (Exception e) {
            logger.error("Error scheduling maintenance", e);
            return createErrorResponse("Error scheduling maintenance: " + e.getMessage());
        }
    }

    @Operation(
        summary = "Get aircraft details",
        description = "Retrieves detailed information about a specific aircraft"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Aircraft details retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied - Insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Aircraft not found")
    })
    @GetMapping("/aircraft/{registrationNumber}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getAircraftDetails(
            @Parameter(description = "Aircraft registration number", required = true)
            @PathVariable String registrationNumber) {
        return flightOperationsService.getAircraft(registrationNumber)
            .map(aircraft -> {
                Map<String, Object> response = new HashMap<>();
                response.put(SUCCESS_KEY, true); // Use constant instead of string literal
                response.put(AIRCRAFT_KEY, aircraft);
                return ResponseEntity.ok(response);
            })
            .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
        summary = "Get aircraft maintenance history",
        description = "Retrieves the maintenance history for a specific aircraft"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Maintenance history retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied - Insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Aircraft not found")
    })
    @GetMapping("/aircraft/{registrationNumber}/maintenance")
    @ResponseBody
    public ResponseEntity<List<MaintenanceRecord>> getMaintenanceHistory(
            @Parameter(description = "Aircraft registration number", required = true)
            @PathVariable String registrationNumber) {
        logger.info("Retrieving maintenance history for aircraft: {}", registrationNumber);
        
        try {
            List<MaintenanceRecord> history = flightOperationsService.getMaintenanceRecords(registrationNumber);
            
            if (history.isEmpty()) {
                logger.info("No maintenance records found for aircraft: {}", registrationNumber);
            } else {
                logger.info("Found {} maintenance records for aircraft: {}", history.size(), registrationNumber);
            }
            
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            logger.error("Error retrieving maintenance history for {}", registrationNumber, e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @Operation(
        summary = "Update maintenance record status",
        description = "Updates the status of an existing maintenance record"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Maintenance status updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid status"),
        @ApiResponse(responseCode = "403", description = "Access denied - Insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Maintenance record not found")
    })
    @PostMapping("/maintenance/{recordId}/status")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateMaintenanceStatus(
            @Parameter(description = "Maintenance record ID", required = true)
            @PathVariable String recordId,
            @Parameter(description = "New maintenance status", required = true)
            @RequestParam String status) {
        
        logger.info("Updating maintenance record status - Record ID: {}, Status: {}", recordId, status);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            MaintenanceRecord.MaintenanceStatus newStatus = MaintenanceRecord.MaintenanceStatus.valueOf(status);
            boolean updated = maintenanceRecordService.updateMaintenanceStatus(recordId, newStatus);
            
            response.put(SUCCESS_KEY, updated);
            response.put(MESSAGE_KEY, updated ? 
                "Maintenance status updated successfully" : 
                "Failed to update maintenance status");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid maintenance status: {}", status, e);
            return createErrorResponse("Invalid maintenance status");
        } catch (Exception e) {
            logger.error("Error updating maintenance status", e);
            return createErrorResponse("Error updating maintenance status: " + e.getMessage());
        }
    }
    
    @Operation(
        summary = "Complete maintenance record",
        description = "Marks a maintenance record as completed with optional completion notes"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Maintenance record completed successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied - Insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Maintenance record not found")
    })
    @PostMapping("/maintenance/{recordId}/complete")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> completeMaintenanceRecord(
            @Parameter(description = "Maintenance record ID", required = true)
            @PathVariable String recordId,
            @Parameter(description = "Completion notes", required = false)
            @RequestParam(required = false) String notes) {
        
        logger.info("Completing maintenance record - Record ID: {}", recordId);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            boolean completed = maintenanceRecordService.completeMaintenanceRecord(recordId, LocalDateTime.now(), notes);
            
            response.put(SUCCESS_KEY, completed);
            response.put(MESSAGE_KEY, completed ? 
                "Maintenance record completed successfully" : 
                "Failed to complete maintenance record");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error completing maintenance record", e);
            return createErrorResponse("Error completing maintenance record: " + e.getMessage());
        }
    }

    @Operation(
        summary = "Get gate assignments",
        description = "Retrieves all assignments for a specific gate"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Gate assignments retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied - Insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Gate not found")
    })
    @GetMapping("/gates/{gateId}/assignments")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getGateAssignments(
            @Parameter(description = "Gate ID", required = true)
            @PathVariable String gateId) {
        logger.info("Retrieving assignments for gate: {}", gateId);
        
        Map<String, Object> response = new HashMap<>();
        List<AssignmentModel> assignments = assignmentService.getAssignmentsForGate(gateId);
        response.put(ASSIGNMENTS_KEY, assignments);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Create gate assignment",
        description = "Creates a new gate assignment for a flight"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Gate assignment created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid assignment data or time conflict"),
        @ApiResponse(responseCode = "403", description = "Access denied - Insufficient permissions")
    })
    @PostMapping("/gates/assignments/create")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createAssignment(
            @Parameter(description = "Gate assignment details", required = true)
            @RequestBody @Valid AssignmentModel assignment) {
        logger.info("Creating new assignment for gate: {}", assignment.getGateId());
        
        Map<String, Object> response = new HashMap<>();
        boolean created = assignmentService.createAssignment(assignment);
        
        if (created) {
            response.put(SUCCESS_KEY, true);
            response.put(MESSAGE_KEY, "Gate assignment created successfully");
        } else {
            response.put(SUCCESS_KEY, false);
            response.put(MESSAGE_KEY, "Failed to create assignment - time conflict detected");
        }
        
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Update gate assignment",
        description = "Updates an existing gate assignment"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Gate assignment updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid assignment data or time conflict"),
        @ApiResponse(responseCode = "403", description = "Access denied - Insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Assignment not found")
    })
    @PutMapping("/gates/{gateId}/assignments/{assignmentId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateAssignment(
            @Parameter(description = "Gate ID", required = true)
            @PathVariable String gateId,
            @Parameter(description = "Assignment ID", required = true)
            @PathVariable Long assignmentId,
            @Parameter(description = "Updated assignment details", required = true)
            @RequestBody @Valid AssignmentModel updated) {
        logger.info("Updating assignment {} for gate {}", assignmentId, gateId);
        
        Map<String, Object> response = new HashMap<>();
        boolean updated_ok = assignmentService.updateAssignment(gateId, assignmentId, updated);
        
        if (updated_ok) {
            response.put(SUCCESS_KEY, true);
            response.put(MESSAGE_KEY, "Assignment updated successfully");
        } else {
            response.put(SUCCESS_KEY, false);
            response.put(MESSAGE_KEY, "Failed to update assignment - not found or conflict detected");
        }
        
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Delete gate assignment",
        description = "Removes a gate assignment"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Gate assignment deleted successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied - Insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Assignment not found")
    })
    @DeleteMapping("/gates/{gateId}/assignments/{assignmentId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteAssignment(
            @Parameter(description = "Gate ID", required = true)
            @PathVariable String gateId,
            @Parameter(description = "Assignment ID", required = true)
            @PathVariable Long assignmentId) {
        logger.info("Deleting assignment {} from gate {}", assignmentId, gateId);
        
        Map<String, Object> response = new HashMap<>();
        boolean deleted = assignmentService.deleteAssignment(gateId, assignmentId);
        
        if (deleted) {
            response.put(SUCCESS_KEY, true);
            response.put(MESSAGE_KEY, "Assignment deleted successfully");
        } else {
            response.put(SUCCESS_KEY, false);
            response.put(MESSAGE_KEY, "Failed to delete assignment - not found");
        }
        
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Update gate assignment status",
        description = "Updates the status of an existing gate assignment"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Assignment status updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid status"),
        @ApiResponse(responseCode = "403", description = "Access denied - Insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Assignment not found")
    })
    @PutMapping("/gates/{gateId}/assignments/{assignmentId}/status/{status}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateAssignmentStatus(
            @Parameter(description = "Gate ID", required = true)
            @PathVariable String gateId,
            @Parameter(description = "Assignment ID", required = true)
            @PathVariable Long assignmentId,
            @Parameter(description = "New assignment status", required = true)
            @PathVariable String status) {
        logger.info("Updating status of assignment {} to {}", assignmentId, status);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            AssignmentStatus newStatus = AssignmentStatus.valueOf(status);
            
            boolean updated = assignmentService.updateAssignmentField(
                gateId, 
                assignmentId, 
                assignment -> assignment.updateStatus(newStatus)
            );
            
            if (updated) {
                response.put(SUCCESS_KEY, true);
                response.put(MESSAGE_KEY, "Assignment status updated successfully");
            } else {
                response.put(SUCCESS_KEY, false);
                response.put(MESSAGE_KEY, "Failed to update assignment status - not found");
            }
        } catch (IllegalArgumentException e) {
            response.put(SUCCESS_KEY, false);
            response.put(MESSAGE_KEY, "Invalid status value: " + status);
        }
        
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Get current gate assignments",
        description = "Retrieves current assignments for all gates"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Current assignments retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied - Insufficient permissions")
    })
    @GetMapping("/gates/assignments/current")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getCurrentAssignments() {
        logger.info("Retrieving current assignments for all gates");
        
        Map<String, Object> response = new HashMap<>();
        Map<String, AssignmentModel> currentAssignments = assignmentService.getCurrentAssignments();
        response.put("currentAssignments", currentAssignments);
        return ResponseEntity.ok(response);
    }

    /**
     * Helper method to create standardized response entities
     * 
     * @param success Operation success flag
     * @param successMessage Message for successful operation
     * @param errorMessage Message for failed operation
     * @return Standardized response entity
     */
    private ResponseEntity<Map<String, Object>> createResponse(boolean success, String successMessage, 
                                           String errorMessage) {
        Map<String, Object> response = new HashMap<>();
        response.put(SUCCESS_KEY, success);
        response.put(MESSAGE_KEY, success ? successMessage : errorMessage);
        return ResponseEntity.ok(response);
    }

    /**
     * Helper method for creating error responses
     * 
     * @param message Error message
     * @return Standardized error response entity
     */
    private ResponseEntity<Map<String, Object>> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put(SUCCESS_KEY, false);
        response.put(MESSAGE_KEY, message);
        return ResponseEntity.badRequest().body(response);
    }
}