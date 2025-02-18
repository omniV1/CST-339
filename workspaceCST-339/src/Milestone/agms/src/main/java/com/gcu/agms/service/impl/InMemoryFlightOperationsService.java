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
import org.springframework.stereotype.Service;

import com.gcu.agms.model.flight.AircraftModel;
import com.gcu.agms.model.flight.AircraftType;
import com.gcu.agms.model.flight.FlightModel;
import com.gcu.agms.model.maintenance.MaintenanceRecord;
import com.gcu.agms.model.maintenance.MaintenanceRecord.MaintenanceStatus;
import com.gcu.agms.model.maintenance.MaintenanceRecord.MaintenanceType;
import com.gcu.agms.service.flight.FlightOperationsService;

import jakarta.annotation.PostConstruct;

@Service
public class InMemoryFlightOperationsService implements FlightOperationsService {
    private static final Logger logger = LoggerFactory.getLogger(InMemoryFlightOperationsService.class);
    
    // In-memory storage
    private final Map<String, FlightModel> flights = new HashMap<>();
    private final Map<String, AircraftModel> aircraft = new HashMap<>();
    private final Map<String, List<MaintenanceRecord>> maintenanceHistory = new HashMap<>();

    @PostConstruct
    public void initialize() {
        logger.info("Initializing flight operations service with sample data");
        
        // Create some sample aircraft
        registerAircraft(new AircraftModel("N12345", "Boeing 737-800", AircraftType.NARROW_BODY));
        registerAircraft(new AircraftModel("N67890", "Airbus A320", AircraftType.NARROW_BODY));
        registerAircraft(new AircraftModel("N11223", "Boeing 777-300", AircraftType.WIDE_BODY));
        
        logger.info("Initialized with {} aircraft", aircraft.size());
    }

    @Override
    public boolean registerAircraft(AircraftModel aircraft) {
        logger.info("Registering new aircraft: {}", aircraft.getRegistrationNumber());
        
        if (this.aircraft.containsKey(aircraft.getRegistrationNumber())) {
            logger.warn("Aircraft already registered: {}", aircraft.getRegistrationNumber());
            return false;
        }
        
        this.aircraft.put(aircraft.getRegistrationNumber(), aircraft);
        logger.info("Aircraft registered successfully");
        return true;
    }

    @Override
    public Optional<AircraftModel> getAircraft(String registrationNumber) {
        return Optional.ofNullable(aircraft.get(registrationNumber));
    }

    @Override
    public List<AircraftModel> getAllAircraft() {
        return new ArrayList<>(aircraft.values());
    }

    @Override
    public boolean updateAircraftStatus(String registrationNumber, 
                                      AircraftModel.AircraftStatus newStatus, 
                                      String location) {
        logger.info("Updating aircraft {} status to {} at location {}", 
                   registrationNumber, newStatus, location);
        
        AircraftModel aircraftModel = aircraft.get(registrationNumber);
        if (aircraftModel == null) {
            logger.warn("Aircraft not found: {}", registrationNumber);
            return false;
        }

        aircraftModel.setStatus(newStatus);
        aircraftModel.setCurrentLocation(location);
        return true;
    }

    @Override
    public boolean updateFlight(FlightModel flight) {
        logger.info("Creating/Updating flight: {}", flight.getFlightNumber());
        
        try {
            // Validate aircraft assignment
            if (flight.getAssignedAircraft() != null) {
                AircraftModel assignedAircraft = this.aircraft.get(flight.getAssignedAircraft());
                if (assignedAircraft == null) {
                    logger.warn("Invalid aircraft assignment: {}", flight.getAssignedAircraft());
                    return false;
                }
                
                // Verify aircraft is available
                if (!assignedAircraft.isAvailableForService()) {
                    logger.warn("Aircraft {} is not available for service", flight.getAssignedAircraft());
                    return false;
                }
            }
            
            // Set initial status if not set
            if (flight.getStatus() == null) {
                flight.setStatus(FlightModel.FlightStatus.SCHEDULED);
            }
            
            // Store the flight
            flights.put(flight.getFlightNumber(), flight);
            logger.info("Successfully created/updated flight: {}", flight.getFlightNumber());
            return true;
            
        } catch (Exception e) {
            logger.error("Error creating/updating flight: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public Map<String, Object> getFlightDetails(String flightNumber) {
        logger.info("Retrieving details for flight {}", flightNumber);
        
        Map<String, Object> details = new HashMap<>();
        FlightModel flight = flights.get(flightNumber);
        
        if (flight != null) {
            details.put("flight", flight);
            details.put("status", flight.getStatus());
            details.put("origin", flight.getOrigin());
            details.put("destination", flight.getDestination());
            details.put("scheduledDeparture", flight.getScheduledDeparture());
            details.put("scheduledArrival", flight.getScheduledArrival());
            
            if (flight.getAssignedAircraft() != null) {
                AircraftModel assignedAircraft = aircraft.get(flight.getAssignedAircraft());
                if (assignedAircraft != null) {
                    details.put("aircraft", assignedAircraft);
                    details.put("currentLocation", assignedAircraft.getCurrentLocation());
                }
            }
        }
        
        return details;
    }

    @Override
    public List<Map<String, Object>> getActiveFlights() {
        List<Map<String, Object>> activeFlights = new ArrayList<>();
        
        // Convert flight data to the format expected by the view
        flights.values().forEach(flight -> {
            Map<String, Object> flightInfo = new HashMap<>();
            flightInfo.put("flightNumber", flight.getFlightNumber());
            flightInfo.put("airlineCode", flight.getAirlineCode());
            flightInfo.put("origin", flight.getOrigin());
            flightInfo.put("destination", flight.getDestination());
            flightInfo.put("aircraft", flight.getAssignedAircraft());
            flightInfo.put("status", flight.getStatus());
            flightInfo.put("scheduledDeparture", flight.getScheduledDeparture());
            flightInfo.put("scheduledArrival", flight.getScheduledArrival());
            
            // Add current location if available
            if (flight.getCurrentLocation() != null) {
                flightInfo.put("currentLocation", flight.getCurrentLocation());
            }
            
            activeFlights.add(flightInfo);
        });
        
        return activeFlights;
    }

    @Override
    public Map<String, Integer> getOperationalStatistics() {
        Map<String, Integer> stats = new HashMap<>();
        
        // Count total flights and active flights
        stats.put("totalFlights", flights.size());
        stats.put("activeFlights", (int) flights.values()
            .stream()
            .filter(FlightModel::isActive)
            .count());
            
        // Count delayed flights
        stats.put("delayedFlights", (int) flights.values()
            .stream()
            .filter(FlightModel::isDelayed)
            .count());
            
        // Count aircraft by availability
        stats.put("totalAircraft", aircraft.size());
        stats.put("availableAircraft", (int) aircraft.values()
            .stream()
            .filter(AircraftModel::isAvailableForService)
            .count());
        
        // Add maintenance count
        stats.put("maintenanceCount", (int) aircraft.values()
            .stream()
            .filter(a -> a.getStatus() == AircraftModel.AircraftStatus.MAINTENANCE)
            .count());
        
        return stats;
    }

    @Override
    public boolean updateFlightStatus(String flightNumber, String newStatus, String location) {
        logger.info("Updating flight {} status to {} at location {}", 
                   flightNumber, newStatus, location);
        
        FlightModel flight = flights.get(flightNumber);
        if (flight == null) {
            logger.warn("Flight not found: {}", flightNumber);
            return false;
        }

        flight.setStatus(FlightModel.FlightStatus.valueOf(newStatus));
        return true;
    }

    @Override
    public boolean deleteFlight(String flightNumber) {
        logger.info("Attempting to delete flight: {}", flightNumber);
        
        try {
            if (!flights.containsKey(flightNumber)) {
                logger.warn("Flight not found: {}", flightNumber);
                return false;
            }
            
            FlightModel flight = flights.get(flightNumber);
            
            // If aircraft is assigned, update its status
            if (flight.getAssignedAircraft() != null) {
                AircraftModel aircraft = this.aircraft.get(flight.getAssignedAircraft());
                if (aircraft != null) {
                    aircraft.setStatus(AircraftModel.AircraftStatus.AVAILABLE);
                    logger.info("Updated aircraft {} status to AVAILABLE", aircraft.getRegistrationNumber());
                }
            }
            
            flights.remove(flightNumber);
            logger.info("Successfully deleted flight: {}", flightNumber);
            return true;
            
        } catch (Exception e) {
            logger.error("Error deleting flight: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public boolean scheduleMaintenance(String registrationNumber, 
                                     LocalDateTime maintenanceDate,
                                     String maintenanceType,
                                     String description) {
        logger.info("Scheduling maintenance - Details: Registration={}, Date={}, Type={}, Description={}",
            registrationNumber, maintenanceDate, maintenanceType, description);
        
        try {
            if (registrationNumber == null || registrationNumber.trim().isEmpty()) {
                logger.warn("Invalid registration number provided");
                return false;
            }

            AircraftModel aircraft = this.aircraft.get(registrationNumber);
            if (aircraft == null) {
                logger.warn("Aircraft not found: {}", registrationNumber);
                return false;
            }

            // Create maintenance record
            MaintenanceRecord record = new MaintenanceRecord();
            record.setRecordId(UUID.randomUUID().toString());
            record.setRegistrationNumber(registrationNumber);
            record.setScheduledDate(maintenanceDate);
            record.setType(MaintenanceType.valueOf(maintenanceType));
            record.setDescription(description);
            record.setStatus(MaintenanceStatus.SCHEDULED);

            // Update aircraft status
            aircraft.setStatus(AircraftModel.AircraftStatus.MAINTENANCE);
            aircraft.setNextMaintenanceDue(maintenanceDate);

            // Store maintenance record
            maintenanceHistory.computeIfAbsent(registrationNumber, k -> new ArrayList<>())
                             .add(record);

            logger.info("Maintenance scheduled successfully for aircraft: {}", registrationNumber);
            return true;
        } catch (Exception e) {
            logger.error("Error scheduling maintenance: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public List<MaintenanceRecord> getMaintenanceRecords(String registrationNumber) {
        return maintenanceHistory.getOrDefault(registrationNumber, new ArrayList<>());
    }
}