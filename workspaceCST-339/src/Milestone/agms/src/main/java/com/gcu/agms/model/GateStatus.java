package com.gcu.agms.model;

/**
 * Defines all possible states a gate can be in within the AGMS system.
 * According to the UML class diagram, this enum maintains a many-to-one relationship
 * with the Gate class, where each gate must have exactly one status at any time.
 * 
 * The statuses represent different operational states of a gate:
 * - AVAILABLE: Ready for assignment
 * - OCCUPIED: Currently in use by a flight
 * - MAINTENANCE: Under maintenance and unavailable
 * - CLOSED: Not operational
 */
public enum GateStatus {
    AVAILABLE("Gate is available for assignment"),
    OCCUPIED("Gate is currently in use"),
    MAINTENANCE("Gate is under maintenance"),
    CLOSED("Gate is not operational");
    
    private final String description;
    
    /**
     * Creates a new GateStatus with a descriptive message.
     * @param description A human-readable description of this status
     */
    GateStatus(String description) {
        this.description = description;
    }
    
    /**
     * Gets the human-readable description of this status.
     * @return The description of what this status means
     */
    public String getDescription() {
        return description;
    }
}