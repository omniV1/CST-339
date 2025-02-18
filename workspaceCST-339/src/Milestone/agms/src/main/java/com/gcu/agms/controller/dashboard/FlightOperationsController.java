package com.gcu.agms.controller.dashboard;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
import com.gcu.agms.model.maintenance.MaintenanceRecord;
import com.gcu.agms.service.flight.FlightOperationsService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

/**
 * Controller handling all flight operations related endpoints in the AGMS system.
 * Provides REST endpoints for managing flights, aircraft, and maintenance operations.
 */
@Controller
@RequestMapping("/operations")
public class FlightOperationsController {
    private static final Logger logger = LoggerFactory.getLogger(FlightOperationsController.class);
    
    private final FlightOperationsService flightOperationsService;

    /**
     * Constructor injection of FlightOperationsService
     * @param flightOperationsService Service handling flight operations logic
     */
    public FlightOperationsController(FlightOperationsService flightOperationsService) {
        this.flightOperationsService = flightOperationsService;
    }

    /**
     * Displays the main operations dashboard
     * Requires OPERATIONS_MANAGER role for access
     * 
     * @param model Spring MVC model for view data
     * @param session HTTP session for user role verification
     * @return View name or redirect to login if unauthorized
     */
    @GetMapping("/dashboard")
    public String showDashboard(Model model, HttpSession session) {
        // Verify operations manager role
        String userRole = (String) session.getAttribute("userRole");
        if (!"OPERATIONS_MANAGER".equals(userRole)) {
            logger.warn("Unauthorized access attempt to operations dashboard");
            return "redirect:/login";
        }

        try {
            // Get flight operations data
            List<Map<String, Object>> activeFlights = flightOperationsService.getActiveFlights();
            logger.info("Retrieved {} active flights", activeFlights.size());
            
            Map<String, Integer> statistics = flightOperationsService.getOperationalStatistics();
            List<AircraftModel> aircraft = flightOperationsService.getAllAircraft();
            
            List<AircraftModel> availableAircraft = aircraft.stream()
                .filter(AircraftModel::isAvailableForService)
                .collect(Collectors.toList());
            
            logger.info("Dashboard data: activeFlights={}, totalAircraft={}, availableAircraft={}", 
                activeFlights.size(), aircraft.size(), availableAircraft.size());

            // Add all necessary data to the model
            model.addAttribute("activeFlights", activeFlights);
            model.addAttribute("statistics", statistics);
            model.addAttribute("aircraft", aircraft);
            model.addAttribute("availableAircraft", availableAircraft);
            
            return "dashboard/operations";
        } catch (Exception e) {
            logger.error("Error loading dashboard data", e);
            throw e;
        }
    }

    /**
     * Retrieves real-time dashboard data for AJAX updates
     * 
     * @return ResponseEntity containing dashboard statistics, active flights, and aircraft data
     */
    @GetMapping("/dashboard/data")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getDashboardData() {
        Map<String, Object> dashboardData = new HashMap<>();
        
        dashboardData.put("statistics", flightOperationsService.getOperationalStatistics());
        dashboardData.put("activeFlights", flightOperationsService.getActiveFlights());
        dashboardData.put("aircraft", flightOperationsService.getAllAircraft());
        
        return ResponseEntity.ok(dashboardData);
    }

    /**
     * Updates the operational status of an aircraft
     * 
     * @param registrationNumber Aircraft registration number
     * @param status New status to set
     * @param location Current location of the aircraft
     * @return Response indicating success or failure
     */
    @PostMapping("/aircraft/update")
    public ResponseEntity<?> updateAircraftStatus(
            @RequestParam String registrationNumber,
            @RequestParam AircraftModel.AircraftStatus status,
            @RequestParam String location) {
        
        boolean updated = flightOperationsService.updateAircraftStatus(
            registrationNumber, status, location);
            
        return createResponse(updated, "Aircraft status updated successfully", 
                            "Failed to update aircraft status");
    }

    /**
     * Creates a new flight in the system
     * 
     * @param flight Flight details from request body
     * @return Response containing creation status and flight details
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
            // Validate flight data
            if (flight.getFlightNumber() == null || flight.getAirlineCode() == null) {
                logger.warn("Invalid flight data: missing required fields");
                response.put("success", false);
                response.put("message", "Missing required flight information");
                return ResponseEntity.badRequest().body(response);
            }

            boolean created = flightOperationsService.updateFlight(flight);
            
            if (created) {
                logger.info("Successfully created flight: {}", flight.getFlightNumber());
                
                // Get updated flight data for verification
                List<Map<String, Object>> activeFlights = flightOperationsService.getActiveFlights();
                logger.info("Current active flights count: {}", activeFlights.size());
                
                response.put("success", true);
                response.put("message", "Flight created successfully");
                response.put("flightNumber", flight.getFlightNumber());
                return ResponseEntity.ok(response);
            } else {
                logger.warn("Failed to create flight: {}", flight.getFlightNumber());
                response.put("success", false);
                response.put("message", "Failed to create flight");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            logger.error("Error creating flight: {} - {}", flight.getFlightNumber(), e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Error creating flight: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Updates the status of an existing flight
     * 
     * @param flightNumber Flight identifier
     * @param status New flight status
     * @param location Optional current location
     * @return Response indicating update success or failure
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
            boolean updated = flightOperationsService.updateFlightStatus(flightNumber, status, location);
            
            if (updated) {
                response.put("success", true);
                response.put("message", "Flight status updated successfully");
            } else {
                response.put("success", false);
                response.put("message", "Failed to update flight status");
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error updating flight status: {}", e.getMessage());
            response.put("success", false);
            response.put("message", "Error updating flight status: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Updates existing flight details
     * 
     * @param flight Updated flight information
     * @return Response indicating update success or failure
     */
    @PutMapping("/flights/update")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateFlight(@RequestBody @Valid FlightModel flight) {
        logger.info("Received request to update flight: {}", flight.getFlightNumber());
        Map<String, Object> response = new HashMap<>();
        
        try {
            boolean updated = flightOperationsService.updateFlight(flight);
            response.put("success", updated);
            response.put("message", updated ? "Flight updated successfully" : "Failed to update flight");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error updating flight", e);
            response.put("success", false);
            response.put("message", "Error updating flight: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Retrieves detailed information about a specific flight
     * 
     * @param flightNumber Flight identifier
     * @return Flight details or 404 if not found
     */
    @GetMapping("/flights/{flightNumber}")
    public ResponseEntity<?> getFlightDetails(@PathVariable String flightNumber) {
        Map<String, Object> details = flightOperationsService.getFlightDetails(flightNumber);
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
            response.put("success", deleted);
            response.put("message", deleted ? "Flight deleted successfully" : "Failed to delete flight");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error deleting flight", e);
            response.put("success", false);
            response.put("message", "Error deleting flight: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
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
            
            response.put("success", scheduled);
            response.put("message", scheduled ? 
                "Maintenance scheduled successfully" : 
                "Failed to schedule maintenance");
            return ResponseEntity.ok(response);
        } catch (DateTimeParseException e) {
            logger.error("Error parsing maintenance date: {}", maintenanceDate, e);
            response.put("success", false);
            response.put("message", "Invalid date format. Please use format: YYYY-MM-DD HH:mm:ss");
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            logger.error("Error scheduling maintenance", e);
            response.put("success", false);
            response.put("message", "Error scheduling maintenance: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
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
    public ResponseEntity<?> getAircraftDetails(@PathVariable String registrationNumber) {
        Optional<AircraftModel> aircraft = flightOperationsService.getAircraft(registrationNumber);
        if (aircraft.isPresent()) {
            return ResponseEntity.ok(aircraft.get());
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Helper method to create standardized response entities
     * 
     * @param success Operation success flag
     * @param successMessage Message for successful operation
     * @param errorMessage Message for failed operation
     * @return Standardized response entity
     */
    private ResponseEntity<?> createResponse(boolean success, String successMessage, 
                                           String errorMessage) {
        return ResponseEntity.ok(Map.of(
            "success", success,
            "message", success ? successMessage : errorMessage
        ));
    }

    /**
     * Retrieves maintenance history for an aircraft
     * @param registrationNumber The registration number of the aircraft
     * @return ResponseEntity containing list of maintenance records or 404 if not found
     */
    @GetMapping("/aircraft/{registrationNumber}/maintenance")
    @ResponseBody
    public ResponseEntity<?> getMaintenanceHistory(@PathVariable String registrationNumber) {
        logger.info("Retrieving maintenance history for aircraft: {}", registrationNumber);
        
        try {
            List<MaintenanceRecord> history = flightOperationsService.getMaintenanceRecords(registrationNumber);
            if (history.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            logger.error("Error retrieving maintenance history for {}", registrationNumber, e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Error retrieving maintenance history: " + e.getMessage()
            ));
        }
    }
}