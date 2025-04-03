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

    /**
     * Displays the main operations dashboard view.
     * 
     * This endpoint renders the primary interface for operations managers, providing
     * a comprehensive overview of:
     * - Active flights currently in the system
     * - Operational statistics (flights by status, on-time performance, etc.)
     * - Aircraft status and availability
     * - Real-time gate assignment information
     * 
     * The dashboard serves as the central hub for monitoring and managing all
     * flight operations activities.
     * 
     * @param model Spring MVC Model for passing data to the view template
     * @param session HTTP session for user context and role verification
     * @return The logical view name for the operations dashboard template
     */
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

    /**
     * Provides real-time dashboard data for AJAX updates.
     * 
     * This endpoint returns JSON data for asynchronous updates to the dashboard
     * without requiring a full page reload. It's used by JavaScript in the dashboard
     * to periodically refresh the displayed information with the latest data.
     * 
     * The returned data includes:
     * - Current operational statistics
     * - Active flight information
     * - Aircraft status updates
     * 
     * This supports a responsive, real-time monitoring experience for operations managers.
     * 
     * @return ResponseEntity with a map containing dashboard data in JSON format
     */
    @GetMapping("/dashboard/data")
    @ResponseBody  // This endpoint returns JSON data for AJAX
    public ResponseEntity<Map<String, Object>> getDashboardData() {
        logger.debug("Fetching real-time dashboard data for AJAX update");
        
        Map<String, Object> dashboardData = new HashMap<>();
        
        // Compile all required dashboard data into a single response
        dashboardData.put(STATISTICS_KEY, flightOperationsService.getOperationalStatistics());
        dashboardData.put(ACTIVE_FLIGHTS_KEY, flightOperationsService.getActiveFlights());
        dashboardData.put(AIRCRAFT_KEY, flightOperationsService.getAllAircraft());
        
        return ResponseEntity.ok(dashboardData);
    }

    /**
     * Updates the operational status of an aircraft.
     * 
     * This endpoint allows operations managers to change an aircraft's status
     * (e.g., IN_SERVICE, MAINTENANCE, OUT_OF_SERVICE) and update its current
     * location. These updates are critical for:
     * - Tracking aircraft availability for flight assignments
     * - Monitoring fleet operational status
     * - Ensuring accurate location data for operational planning
     * 
     * @param registrationNumber Unique identifier for the aircraft
     * @param status New operational status to set
     * @param location Current physical location of the aircraft
     * @return ResponseEntity with success/failure information in JSON format
     */
    @PostMapping("/aircraft/update")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateAircraftStatus(
            @RequestParam String registrationNumber,
            @RequestParam AircraftModel.AircraftStatus status,
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

    /**
     * Creates a new flight in the system.
     * 
     * This endpoint processes flight creation requests, enabling operations managers
     * to add new flights to the system. It handles:
     * - Validation of required flight information
     * - Creation of the flight record in the database
     * - Association with assigned aircraft if specified
     * - Initial status setting (typically SCHEDULED)
     * 
     * Flight creation is a fundamental operation that initiates the flight lifecycle
     * in the system, making it available for gate assignments and operational tracking.
     * 
     * @param flight Flight details from request body including flight number, airline,
     *              origin/destination, and scheduling information
     * @return ResponseEntity with creation status, messages, and the flight identifier
     */
    @PostMapping("/flights/create")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createFlight(@RequestBody @Valid FlightModel flight) {
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

    /**
     * Updates the status of an existing flight.
     * 
     * This endpoint enables operations managers to transition flights through their
     * lifecycle by updating their operational status. Status changes include:
     * - SCHEDULED → BOARDING → DEPARTED → EN_ROUTE → APPROACHING → LANDED → ARRIVED
     * - Status changes to DELAYED, CANCELLED, or DIVERTED for irregular operations
     * 
     * Status updates trigger various operational workflows and notifications in the system.
     * For active flights, the location parameter can be used to track the flight's position.
     * 
     * @param flightNumber The flight identifier (airline code + flight number)
     * @param status The new status to set for the flight
     * @param location Optional current location of the flight (for active flights)
     * @return ResponseEntity with update status and result message
     */
    @PostMapping("/flights/status")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateFlightStatus(
            @RequestParam String flightNumber,
            @RequestParam String status,
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

    /**
     * Updates existing flight details.
     * 
     * This endpoint allows comprehensive updates to flight information including:
     * - Schedule changes (departure/arrival times)
     * - Aircraft reassignments
     * - Route modifications
     * - Passenger count updates
     * 
     * Unlike the status update endpoint, this allows changing multiple flight
     * attributes in a single operation. It's typically used for schedule changes,
     * aircraft swaps, and other significant modifications to flight details.
     * 
     * @param flight Updated flight information with complete flight details
     * @return ResponseEntity with update status and result message
     */
    @PutMapping("/flights/update")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateFlight(@RequestBody @Valid FlightModel flight) {
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

    /**
     * Retrieves detailed information about a specific flight.
     * 
     * This endpoint provides comprehensive information about a flight including:
     * - Basic flight details (airline, flight number, origin/destination)
     * - Schedule information (departure/arrival times)
     * - Current status and location
     * - Assigned aircraft details
     * - Gate assignments
     * - Passenger information
     * 
     * It's used by the UI to display flight details and by other system
     * components that need complete flight information.
     * 
     * @param flightNumber The flight identifier to retrieve details for
     * @return ResponseEntity with detailed flight information
     */
    @GetMapping("/flights/{flightNumber}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getFlightDetails(@PathVariable String flightNumber) {
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

    /**
     * Deletes a flight from the system
     * 
     * @param flightNumber Flight to delete
     * @return Response indicating deletion success or failure
     */
    @DeleteMapping("/flights/{flightNumber}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteFlight(@PathVariable String flightNumber) {
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

    /**
     * Schedules maintenance for an aircraft
     * 
     * @param registrationNumber Aircraft registration number
     * @param maintenanceDate Scheduled maintenance date
     * @param maintenanceType Type of maintenance to perform
     * @param description Maintenance description
     * @return Response indicating scheduling success or failure
     */
    @PostMapping("/aircraft/maintenance")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> scheduleMaintenance(
            @RequestParam String registrationNumber,
            @RequestParam String maintenanceDate,
            @RequestParam String maintenanceType,
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

    /**
     * Retrieves detailed information about a specific aircraft
     * 
     * @param registrationNumber Aircraft registration number
     * @return Aircraft details or 404 if not found
     */
    @GetMapping("/aircraft/{registrationNumber}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getAircraftDetails(@PathVariable String registrationNumber) {
        return flightOperationsService.getAircraft(registrationNumber)
            .map(aircraft -> {
                Map<String, Object> response = new HashMap<>();
                response.put(SUCCESS_KEY, true); // Use constant instead of string literal
                response.put(AIRCRAFT_KEY, aircraft);
                return ResponseEntity.ok(response);
            })
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Retrieves maintenance history for an aircraft
     * @param registrationNumber The registration number of the aircraft
     * @return ResponseEntity containing list of maintenance records or 404 if not found
     */
    @GetMapping("/aircraft/{registrationNumber}/maintenance")
    @ResponseBody
    public ResponseEntity<List<MaintenanceRecord>> getMaintenanceHistory(@PathVariable String registrationNumber) {
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
    
    /**
     * Updates the status of a maintenance record
     * 
     * @param recordId The record ID of the maintenance record
     * @param status The new status
     * @return Response indicating update success or failure
     */
    @PostMapping("/maintenance/{recordId}/status")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateMaintenanceStatus(
            @PathVariable String recordId,
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
    
    /**
     * Completes a maintenance record
     * 
     * @param recordId The record ID of the maintenance record
     * @param notes Optional notes about the completion
     * @return Response indicating completion success or failure
     */
    @PostMapping("/maintenance/{recordId}/complete")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> completeMaintenanceRecord(
            @PathVariable String recordId,
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

    /**
     * Retrieves all assignments for a gate.
     * 
     * @param gateId The gate ID to get assignments for
     * @return Response containing the list of assignments
     */
    @GetMapping("/gates/{gateId}/assignments")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getGateAssignments(@PathVariable String gateId) {
        logger.info("Retrieving assignments for gate: {}", gateId);
        
        Map<String, Object> response = new HashMap<>();
        List<AssignmentModel> assignments = assignmentService.getAssignmentsForGate(gateId);
        response.put(ASSIGNMENTS_KEY, assignments);
        return ResponseEntity.ok(response);
    }

    /**
     * Creates a new gate assignment.
     * 
     * @param assignment The assignment data
     * @return Response indicating success or failure
     */
    @PostMapping("/gates/assignments/create")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createAssignment(@RequestBody @Valid AssignmentModel assignment) {
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

    /**
     * Updates an existing gate assignment.
     * 
     * @param gateId The gate ID containing the assignment
     * @param assignmentId The ID of the assignment to update
     * @param updated The updated assignment data
     * @return Response indicating success or failure
     */
    @PutMapping("/gates/{gateId}/assignments/{assignmentId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateAssignment(
            @PathVariable String gateId,
            @PathVariable Long assignmentId,
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

    /**
     * Deletes a gate assignment.
     * 
     * @param gateId The gate ID containing the assignment
     * @param assignmentId The ID of the assignment to delete
     * @return Response indicating success or failure
     */
    @DeleteMapping("/gates/{gateId}/assignments/{assignmentId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteAssignment(
            @PathVariable String gateId,
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

    /**
     * Updates the status of a gate assignment.
     * 
     * @param gateId The gate ID containing the assignment
     * @param assignmentId The ID of the assignment to update
     * @param status The new status
     * @return Response indicating success or failure
     */
    @PutMapping("/gates/{gateId}/assignments/{assignmentId}/status/{status}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateAssignmentStatus(
            @PathVariable String gateId,
            @PathVariable Long assignmentId,
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

    /**
     * Gets current assignments for all gates.
     * 
     * @return Response containing current assignments for all gates
     */
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