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
    public List<AircraftModel> getAvailableAircraft() {
        return getAllAircraft().stream()
                .filter(a -> a.getStatus() == AircraftModel.AircraftStatus.AVAILABLE)
                .toList();  // Changed from collect(Collectors.toList())
    }

    @Override
    public boolean registerAircraft(AircraftModel newAircraft) {  // Changed parameter name from 'aircraft' to 'newAircraft'
        if (newAircraft == null || newAircraft.getRegistrationNumber() == null
                || newAircraft.getRegistrationNumber().trim().isEmpty()) {
            logger.warn("Invalid aircraft registration - missing required fields");
            return false;
        }

        logger.info("Registering new aircraft: {}", newAircraft.getRegistrationNumber());

        if (this.aircraft.containsKey(newAircraft.getRegistrationNumber())) {
            logger.warn("Aircraft already registered: {}", newAircraft.getRegistrationNumber());
            return false;
        }

        this.aircraft.put(newAircraft.getRegistrationNumber(), newAircraft);
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
    public boolean updateAircraftStatus(String registrationNumber, AircraftModel.AircraftStatus newStatus, String location) {
        logger.info("Updating aircraft {} status to {} at location {}", registrationNumber, newStatus, location);

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
    public boolean scheduleMaintenance(String registrationNumber, LocalDateTime maintenanceDate, String maintenanceType, String description) {
        logger.info("Scheduling maintenance - Details: Registration={}, Date={}, Type={}, Description={}", registrationNumber, maintenanceDate, maintenanceType, description);

        try {
            if (registrationNumber == null || registrationNumber.trim().isEmpty()) {
                logger.warn("Invalid registration number provided");
                return false;
            }

            AircraftModel aircraftModel = this.aircraft.get(registrationNumber); // Changed from aircraft
            if (aircraftModel == null) {
                logger.warn("Aircraft not found: {}", registrationNumber);
                return false;
            }

            // Create maintenance record
            MaintenanceRecord maintenanceRecord = new MaintenanceRecord();  // Changed from 'record'
            maintenanceRecord.setRecordId(UUID.randomUUID().toString());
            maintenanceRecord.setRegistrationNumber(registrationNumber);
            maintenanceRecord.setScheduledDate(maintenanceDate);
            maintenanceRecord.setType(MaintenanceType.valueOf(maintenanceType));
            maintenanceRecord.setDescription(description);
            maintenanceRecord.setStatus(MaintenanceStatus.SCHEDULED);

            // Update aircraft status
            aircraftModel.setStatus(AircraftModel.AircraftStatus.MAINTENANCE);
            aircraftModel.setNextMaintenanceDue(maintenanceDate);

            // Store maintenance record
            maintenanceHistory.computeIfAbsent(registrationNumber, k -> new ArrayList<>()).add(maintenanceRecord);

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

    @Override
    public boolean createFlight(FlightModel flight) {
        logger.info("Creating new flight: {}", flight.getFlightNumber());

        // Validate required fields
        if (!validateFlight(flight)) {
            logger.warn("Invalid flight data - missing required fields");
            return false;
        }

        if (flights.containsKey(flight.getFlightNumber())) {
            logger.warn("Flight already exists: {}", flight.getFlightNumber());
            return false;
        }

        flights.put(flight.getFlightNumber(), flight);
        logger.info("Flight created successfully");
        return true;
    }

    @Override
    public boolean updateFlight(FlightModel flight) {
        logger.info("Updating flight: {}", flight.getFlightNumber());

        // Validate required fields and times
        if (!validateFlight(flight)) {
            logger.warn("Invalid flight data");
            return false;
        }

        // Update flight
        flights.put(flight.getFlightNumber(), flight);
        logger.info("Flight updated successfully");
        return true;
    }

    private boolean validateFlight(FlightModel flight) {
        return flight != null &&
               flight.getFlightNumber() != null && !flight.getFlightNumber().isEmpty() &&
               flight.getAirlineCode() != null && !flight.getAirlineCode().isEmpty() &&
               flight.getOrigin() != null && !flight.getOrigin().isEmpty() &&
               flight.getDestination() != null && !flight.getDestination().isEmpty() &&
               flight.getScheduledDeparture() != null &&
               flight.getScheduledArrival() != null &&
               flight.getAssignedAircraft() != null &&
               flight.getStatus() != null &&
               flight.getScheduledArrival().isAfter(flight.getScheduledDeparture());
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

        // Filter and convert active flights to view format
        flights.values().stream()
            .filter(flight -> flight.getStatus() != FlightModel.FlightStatus.COMPLETED) // Only non-completed flights
            .forEach(flight -> {
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
            });

        return activeFlights;
    }

    @Override
    public Map<String, Integer> getOperationalStatistics() {
        Map<String, Integer> stats = new HashMap<>();

        // Count total flights
        stats.put("totalFlights", flights.size());

        // Count active flights (SCHEDULED, DELAYED, BOARDING, DEPARTED)
        stats.put("activeFlights", (int) flights.values().stream()
            .filter(f -> f.getStatus() != FlightModel.FlightStatus.COMPLETED && 
                        f.getStatus() != FlightModel.FlightStatus.CANCELLED)
            .count());

        // Count delayed flights
        stats.put("delayedFlights", (int) flights.values().stream()
            .filter(f -> f.getStatus() == FlightModel.FlightStatus.DELAYED)
            .count());

        // Count aircraft by status
        stats.put("totalAircraft", aircraft.size());
        stats.put("availableAircraft", (int) aircraft.values().stream()
            .filter(a -> a.getStatus() == AircraftModel.AircraftStatus.ACTIVE)
            .count());
        stats.put("maintenanceCount", (int) aircraft.values().stream()
            .filter(a -> a.getStatus() == AircraftModel.AircraftStatus.MAINTENANCE)
            .count());

        return stats;
    }

    @Override
    public boolean updateFlightStatus(String flightNumber, String status, String location) {
        if (flightNumber == null || status == null) {
            return false;
        }
        
        FlightModel flight = flights.get(flightNumber);
        if (flight == null) {
            return false;
        }
        
        try {
            flight.setStatus(FlightModel.FlightStatus.valueOf(status));
            flight.setCurrentLocation(location);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
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
                AircraftModel aircraftModel = this.aircraft.get(flight.getAssignedAircraft()); // Changed from aircraft
                if (aircraftModel != null) {
                    aircraftModel.setStatus(AircraftModel.AircraftStatus.AVAILABLE);
                    logger.info("Updated aircraft {} status to AVAILABLE", aircraftModel.getRegistrationNumber());
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
    public List<FlightModel> searchFlights(String origin, String destination, String airline) {
        return flights.values().stream()
            .filter(f -> (origin == null || f.getOrigin().equals(origin)) &&
                        (destination == null || f.getDestination().equals(destination)) &&  
                        (airline == null || f.getAirlineCode().equals(airline)))
            .toList(); // Changed from collect(Collectors.toList())
    }

    @Override
    public boolean createFlights(List<FlightModel> flights) {
        try {
            flights.forEach(flight -> this.flights.put(flight.getFlightNumber(), flight));
            return true;
        } catch (Exception e) {
            logger.error("Error creating flights: {}", e.getMessage());
            return false;
        }
    }

    @Override 
    public boolean updateFlightStatuses(List<String> flightNumbers, String status, String reason) {
        try {
            flightNumbers.forEach(number -> {
                FlightModel flight = flights.get(number);
                if (flight != null) {
                    flight.setStatus(FlightModel.FlightStatus.valueOf(status));
                    flight.setRemarks(reason);
                }
            });
            return true;
        } catch (Exception e) {
            logger.error("Error updating flight statuses: {}", e.getMessage());
            return false; 
        }
    }
}
