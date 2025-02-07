package com.gcu.agms.service.gate;

import java.util.Map;

/**
 * Defines the core operations for managing airport gates in the AGMS system.
 * This interface provides methods to track gate statuses and retrieve operational statistics.
 * The GateStatus enum is part of the interface as it defines the fundamental states
 * that any implementation must support.
 */
public interface GateOperationsService {
    /**
     * Represents the possible states a gate can be in.
     * Each status includes a human-readable label and a CSS class for UI display.
     */
    enum GateStatus {
        AVAILABLE("Available", "success"),
        OCCUPIED("Occupied", "warning"),
        MAINTENANCE("Maintenance", "danger");
        
        private final String label;
        private final String cssClass;
        
        GateStatus(String label, String cssClass) {
            this.label = label;
            this.cssClass = cssClass;
        }
        
        public String getLabel() { return label; }
        public String getCssClass() { return cssClass; }
    }

    /**
     * Retrieves the current status of all gates in the system.
     * @return A map where the key is the gate ID and the value is its current status
     */
    Map<String, GateStatus> getAllGateStatuses();

    /**
     * Generates statistical information about gate usage.
     * @return A map containing various statistics like total gates, available gates, etc.
     */
    Map<String, Integer> getStatistics();

    
}