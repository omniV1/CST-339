package com.gcu.agms.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.gcu.agms.model.flight.AircraftModel;
import com.gcu.agms.model.flight.FlightModel;
import com.gcu.agms.model.maintenance.MaintenanceRecord;
import com.gcu.agms.repository.AircraftRepository;
import com.gcu.agms.repository.FlightRepository;
import com.gcu.agms.service.flight.FlightOperationsService;
import com.gcu.agms.service.maintenance.MaintenanceRecordService;

/**
 * JDBC implementation of the FlightOperationsService interface.
 * This service uses database repositories to access and manage flight and maintenance data.
 */
@Service("jdbcFlightOperationsService")
@Primary
public class JdbcFlightOperationsService implements FlightOperationsService {

    private static final Logger logger = LoggerFactory.getLogger(JdbcFlightOperationsService.class);
    
    private final FlightRepository flightRepository;
    private final AircraftRepository aircraftRepository;
    private final MaintenanceRecordService maintenanceRecordService;

    /**
     * Constructor with repositories dependency injection.
     * 
     * @param flightRepository Repository for flight data access
     * @param aircraftRepository Repository for aircraft data access
     * @param maintenanceRecordService Service for maintenance record operations
     */
    public JdbcFlightOperationsService(
            FlightRepository flightRepository,
            AircraftRepository aircraftRepository,
            MaintenanceRecordService maintenanceRecordService) {
        this.flightRepository = flightRepository;
        this.aircraftRepository = aircraftRepository;
        this.maintenanceRecordService = maintenanceRecordService;
        logger.info("Initialized JDBC Flight Operations Service with maintenance support");
    }

    @Override
    public boolean registerAircraft(AircraftModel aircraft) {
        logger.info("Registering new aircraft: {}", aircraft.getRegistrationNumber());
        
        // Validate required fields
        if (aircraft.getRegistrationNumber() == null || aircraft.getModel() == null || aircraft.getType() == null) {
            logger.warn("Invalid aircraft data - missing required fields");
            return false;
        }
        
        // Check if aircraft already exists
        if (aircraftRepository.existsByRegistrationNumber(aircraft.getRegistrationNumber())) {
            logger.warn("Aircraft with registration number already exists: {}", aircraft.getRegistrationNumber());
            return false;
        }
        
        // Set default status if not provided
        if (aircraft.getStatus() == null) {
            aircraft.setStatus(AircraftModel.AircraftStatus.AVAILABLE);
        }
        
        try {
            aircraftRepository.save(aircraft);
            logger.info("Aircraft registered successfully: {}", aircraft.getRegistrationNumber());
            return true;
        } catch (Exception e) {
            logger.error("Error registering aircraft: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public Optional<AircraftModel> getAircraft(String registrationNumber) {
        logger.debug("Retrieving aircraft with registration number: {}", registrationNumber);
        return aircraftRepository.findByRegistrationNumber(registrationNumber);
    }

    @Override
    public List<AircraftModel> getAllAircraft() {
        logger.debug("Retrieving all aircraft");
        return aircraftRepository.findAll();
    }

    @Override
    public boolean updateAircraftStatus(String registrationNumber, AircraftModel.AircraftStatus newStatus, String location) {
        logger.info("Updating status for aircraft: {} to {} at {}", registrationNumber, newStatus, location);
        
        if (registrationNumber == null || newStatus == null) {
            logger.warn("Invalid parameters for aircraft status update");
            return false;
        }
        
        return aircraftRepository.updateStatus(registrationNumber, newStatus.name(), location);
    }

    @Override
    public boolean scheduleMaintenance(String registrationNumber, LocalDateTime maintenanceDate, String maintenanceType, String description) {
        logger.info("Scheduling maintenance for aircraft: {} on {}", registrationNumber, maintenanceDate);
        
        // Set aircraft status to MAINTENANCE
        boolean statusUpdated = aircraftRepository.updateStatus(
            registrationNumber, 
            AircraftModel.AircraftStatus.MAINTENANCE.name(),
            "Maintenance"
        );
        
        // Update next maintenance date
        boolean maintenanceScheduled = aircraftRepository.updateMaintenanceDate(
            registrationNumber,
            maintenanceDate
        );
        
        // Create a maintenance record
        MaintenanceRecord record = new MaintenanceRecord();
        record.setRecordId(UUID.randomUUID().toString());
        record.setRegistrationNumber(registrationNumber);
        record.setScheduledDate(maintenanceDate);
        
        // Parse the maintenance type string to enum
        try {
            MaintenanceRecord.MaintenanceType type = MaintenanceRecord.MaintenanceType.valueOf(maintenanceType);
            record.setType(type);
        } catch (IllegalArgumentException e) {
            // Default to ROUTINE if the type is invalid
            record.setType(MaintenanceRecord.MaintenanceType.ROUTINE);
        }
        
        record.setStatus(MaintenanceRecord.MaintenanceStatus.SCHEDULED);
        record.setDescription(description);
        
        // Save the maintenance record
        MaintenanceRecord savedRecord = maintenanceRecordService.createMaintenanceRecord(record);
        
        return statusUpdated && maintenanceScheduled && (savedRecord != null);
    }

    @Override
    public List<MaintenanceRecord> getMaintenanceRecords(String registrationNumber) {
        logger.info("Retrieving maintenance records for aircraft: {}", registrationNumber);
        return maintenanceRecordService.getMaintenanceRecordsByAircraft(registrationNumber);
    }

    @Override
    public List<AircraftModel> getAvailableAircraft() {
        logger.debug("Retrieving available aircraft");
        return aircraftRepository.findAvailableAircraft();
    }

    // Other methods remain unchanged
    @Override
    public boolean createFlight(FlightModel flight) {
        logger.info("Creating new flight: {}", flight.getFlightNumber());
        
        // Validate flight data
        if (!validateFlight(flight)) {
            logger.warn("Invalid flight data - missing required fields");
            return false;
        }
        
        // Save flight to database
        try {
            flightRepository.save(flight);
            logger.info("Flight created successfully");
            return true;
        } catch (Exception e) {
            logger.error("Error creating flight: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean createFlights(List<FlightModel> flights) {
        logger.info("Creating multiple flights: {}", flights.size());
        
        try {
            for (FlightModel flight : flights) {
                if (!createFlight(flight)) {
                    // If any flight fails to create, continue but log the error
                    logger.warn("Failed to create flight: {}", flight.getFlightNumber());
                }
            }
            return true;
        } catch (Exception e) {
            logger.error("Error creating flights: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean updateFlight(FlightModel flight) {
        logger.info("Updating flight: {}", flight.getFlightNumber());
        
        // Validate flight data
        if (!validateFlight(flight)) {
            logger.warn("Invalid flight data");
            return false;
        }
        
        // Save flight to database (save handles both insert and update)
        try {
            flightRepository.save(flight);
            logger.info("Flight updated successfully: {}", flight.getFlightNumber());
            return true;
        } catch (Exception e) {
            logger.error("Error updating flight: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean updateFlightStatuses(List<String> flightNumbers, String status, String reason) {
        logger.info("Updating status for multiple flights to: {}", status);
        
        try {
            for (String flightNumber : flightNumbers) {
                if (!updateFlightStatus(flightNumber, status, null)) {
                    // If any update fails, continue but log the error
                    logger.warn("Failed to update status for flight: {}", flightNumber);
                }
            }
            return true;
        } catch (Exception e) {
            logger.error("Error updating flight statuses: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public Map<String, Object> getFlightDetails(String flightNumber) {
        logger.info("Retrieving details for flight: {}", flightNumber);
        
        Map<String, Object> details = new HashMap<>();
        Optional<FlightModel> flightOpt = flightRepository.findByFlightNumber(flightNumber);
        
        if (flightOpt.isPresent()) {
            FlightModel flight = flightOpt.get();
            details.put("flight", flight);
            details.put("status", flight.getStatus());
            details.put("origin", flight.getOrigin());
            details.put("destination", flight.getDestination());
            details.put("scheduledDeparture", flight.getScheduledDeparture());
            details.put("scheduledArrival", flight.getScheduledArrival());
            
            if (flight.getAssignedAircraft() != null) {
                // If we have aircraft data in the future, we would add it here
                details.put("aircraft", flight.getAssignedAircraft());
                details.put("currentLocation", flight.getCurrentLocation());
            }
        } else {
            logger.warn("Flight not found: {}", flightNumber);
        }
        
        return details;
    }

    @Override
    public List<Map<String, Object>> getActiveFlights() {
        logger.info("Retrieving active flights");
        
        List<Map<String, Object>> activeFlights = new ArrayList<>();
        List<FlightModel> flights = flightRepository.findActiveFlights();
        
        // If there are no active flights in the database, try to get all flights
        if (flights.isEmpty()) {
            flights = flightRepository.findAll();
        }
        
        // Filter and convert active flights to view format
        for (FlightModel flight : flights) {
            if (flight.getStatus() == FlightModel.FlightStatus.COMPLETED) {
                continue; // Skip completed flights
            }
            
            Map<String, Object> flightInfo = new HashMap<>();
            flightInfo.put("flight", flight);  // Store whole flight object
            flightInfo.put("flightNumber", flight.getFlightNumber());
            flightInfo.put("airlineCode", flight.getAirlineCode());
            flightInfo.put("origin", flight.getOrigin());
            flightInfo.put("destination", flight.getDestination());
            flightInfo.put("aircraft", flight.getAssignedAircraft());
            flightInfo.put("status", flight.getStatus());
            flightInfo.put("scheduledDeparture", flight.getScheduledDeparture());
            flightInfo.put("scheduledArrival", flight.getScheduledArrival());
            
            if (flight.getCurrentLocation() != null) {
                flightInfo.put("currentLocation", flight.getCurrentLocation());
            }
            
            activeFlights.add(flightInfo);
        }
        
        return activeFlights;
    }

    @Override
    public List<FlightModel> searchFlights(String origin, String destination, String airline) {
        logger.info("Searching flights - Origin: {}, Destination: {}, Airline: {}", origin, destination, airline);
        
        List<FlightModel> allFlights = flightRepository.findAll();
        List<FlightModel> matchingFlights = new ArrayList<>();
        
        for (FlightModel flight : allFlights) {
            boolean matches = true;
            
            if (origin != null && !origin.isEmpty() && !flight.getOrigin().equals(origin)) {
                matches = false;
            }
            
            if (destination != null && !destination.isEmpty() && !flight.getDestination().equals(destination)) {
                matches = false;
            }
            
            if (airline != null && !airline.isEmpty() && !flight.getAirlineCode().equals(airline)) {
                matches = false;
            }
            
            if (matches) {
                matchingFlights.add(flight);
            }
        }
        
        return matchingFlights;
    }

    @Override
    public Map<String, Integer> getOperationalStatistics() {
        logger.info("Calculating operational statistics");
        
        Map<String, Integer> stats = new HashMap<>();
        
        // Flight stats
        stats.put("totalFlights", flightRepository.countAll());
        stats.put("activeFlights", flightRepository.countActiveFlights());
        stats.put("delayedFlights", flightRepository.countDelayedFlights());
        
        // Aircraft stats
        stats.put("totalAircraft", aircraftRepository.countAll());
        stats.put("availableAircraft", aircraftRepository.countByStatus("AVAILABLE"));
        stats.put("maintenanceCount", aircraftRepository.countByStatus("MAINTENANCE"));
        
        logger.info("Operational statistics calculated: {}", stats);
        return stats;
    }

    @Override
    public boolean updateFlightStatus(String flightNumber, String status, String location) {
        logger.info("Updating status for flight: {} to {}", flightNumber, status);
        
        if (flightNumber == null || status == null) {
            logger.warn("Invalid parameters for status update");
            return false;
        }
        
        try {
            Optional<FlightModel> flightOpt = flightRepository.findByFlightNumber(flightNumber);
            
            if (flightOpt.isPresent()) {
                FlightModel flight = flightOpt.get();
                flight.setStatus(FlightModel.FlightStatus.valueOf(status));
                
                if (location != null && !location.isEmpty()) {
                    flight.setCurrentLocation(location);
                }
                
                // Update timestamps based on status
                LocalDateTime now = LocalDateTime.now();
                
                if (status.equals(FlightModel.FlightStatus.DEPARTED.name())) {
                    flight.setActualDeparture(now);
                } else if (status.equals(FlightModel.FlightStatus.ARRIVED.name()) || 
                           status.equals(FlightModel.FlightStatus.COMPLETED.name())) {
                    if (flight.getActualDeparture() == null) {
                        flight.setActualDeparture(now.minusHours(1)); // Set a default
                    }
                    flight.setActualArrival(now);
                }
                
                flightRepository.save(flight);
                logger.info("Flight status updated successfully: {}", flightNumber);
                return true;
            } else {
                logger.warn("Flight not found for status update: {}", flightNumber);
                return false;
            }
        } catch (IllegalArgumentException e) {
            logger.error("Invalid status value: {}", status);
            return false;
        } catch (Exception e) {
            logger.error("Error updating flight status: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean deleteFlight(String flightNumber) {
        logger.info("Deleting flight: {}", flightNumber);
        
        try {
            boolean deleted = flightRepository.deleteByFlightNumber(flightNumber);
            logger.info("Flight deletion result: {}", deleted ? "success" : "failed");
            return deleted;
        } catch (Exception e) {
            logger.error("Error deleting flight: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Validates a flight model for creation and updates.
     * 
     * @param flight The flight model to validate
     * @return true if valid, false otherwise
     */
    private boolean validateFlight(FlightModel flight) {
        return flight != null &&
               flight.getFlightNumber() != null && !flight.getFlightNumber().isEmpty() &&
               flight.getAirlineCode() != null && !flight.getAirlineCode().isEmpty() &&
               flight.getOrigin() != null && !flight.getOrigin().isEmpty() &&
               flight.getDestination() != null && !flight.getDestination().isEmpty() &&
               flight.getScheduledDeparture() != null &&
               flight.getScheduledArrival() != null &&
               flight.getStatus() != null &&
               flight.getScheduledArrival().isAfter(flight.getScheduledDeparture());
    }
}