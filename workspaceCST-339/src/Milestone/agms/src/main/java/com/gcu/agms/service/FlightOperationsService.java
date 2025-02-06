package com.gcu.agms.service;

import java.util.List;
import java.util.Map;

import com.gcu.agms.model.FlightModel;

/**
 * Service interface defining the core operations for flight management.
 * This interface follows the Spring Framework's dependency injection principles,
 * allowing for different implementations while maintaining loose coupling.
 */
public interface FlightOperationsService {
    /**
     * Retrieves current flight operation statistics.
     * @return Map containing metrics like available gates, occupied gates, etc.
     */
    Map<String, Integer> getOperationalStatistics();

     /**
     * Retrieves real-time gate utilization data.
     * @return Map containing current gate statuses and metrics
     */
    Map<String, Object> getGateUtilization();
    
    /**
     * Requests a gate change for a specific flight.
     * @param flightNumber The flight requiring a gate change
     * @param currentGate Current gate assignment
     * @param requestedGate Desired new gate
     * @param reason Justification for the change
     * @return true if request was successfully submitted
     */
    boolean requestGateChange(String flightNumber, String currentGate, 
                            String requestedGate, String reason);
    
    /**
     * Retrieves the current flight schedule with gate assignments.
     * @return List of flights with their gate assignments
     */
    List<FlightModel> getCurrentSchedule();
    
    /**
     * Creates a new gate for flight operations.
     * @param terminalNumber Terminal where the gate will be located
     * @param gateNumber Specific gate identifier
     * @return true if gate was successfully created
     */
    boolean createNewGate(String terminalNumber, String gateNumber);
    
   

    
}
