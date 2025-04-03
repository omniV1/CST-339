package com.gcu.agms.model.gate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.gcu.agms.model.flight.AircraftType;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * Represents a physical gate in the Airport Gate Management System (AGMS).
 * 
 * This model encapsulates all information about an airport gate, including:
 * - Identification (gate ID, terminal, gate number)
 * - Physical characteristics (size, features, equipment)
 * - Operational status (available, occupied, maintenance)
 * - Compatibility information (aircraft types, international/domestic)
 * 
 * Gates are a critical resource in the airport, serving as connection points between
 * the terminal building and aircraft. This model supports the core gate assignment
 * functionality, allowing gate managers to allocate appropriate gates to flights
 * based on aircraft type, flight requirements, and gate availability.
 * 
 * The model includes validation annotations to ensure data integrity when gates
 * are created or modified through forms, and provides methods to determine
 * compatibility between gates and aircraft types.
 */
@Data   
public class GateModel {
    /**
     * Unique database identifier for the gate
     */
    private Long id;
    
    /**
     * Gate identifier following the format T#G## where:
     * - T# represents the terminal number (T1-T4)
     * - G## represents the gate number (G1-G99)
     * 
     * Example: T1G5 (Terminal 1, Gate 5)
     */
    @NotEmpty(message = "Gate ID is required")
    @Pattern(regexp = "^T[1-4]G\\d{1,2}$", 
             message = "Gate ID must be in format T#G# (e.g., T1G1)")
    private String gateId;
    
    /**
     * Terminal number (1-4) where the gate is located
     * Used for physical grouping and navigation
     */
    @NotEmpty(message = "Terminal number is required")
    @Pattern(regexp = "^[1-4]$", message = "Terminal must be between 1 and 4")
    private String terminal;
    
    /**
     * Numeric gate identifier within the terminal
     * Typically 1-2 digits (1-99)
     */
    @NotEmpty(message = "Gate number is required")
    @Pattern(regexp = "^\\d{1,2}$", message = "Gate number must be 1-2 digits")
    private String gateNumber;
    
    /**
     * Type of flights the gate can handle (domestic, international, or both)
     * International gates typically require additional security and customs facilities
     */
    @NotNull(message = "Gate type must be specified")
    private GateType gateType = GateType.DOMESTIC;
    
    /**
     * Physical size of the gate, determining what aircraft types it can accommodate
     * Affects compatibility with different aircraft sizes
     */
    @NotNull(message = "Gate size must be specified")
    private GateSize gateSize = GateSize.MEDIUM;
    
    /**
     * Current operational status of the gate
     * Controls whether the gate can be assigned to flights
     */
    @NotNull(message = "Gate status must be specified")
    private GateStatus status = GateStatus.UNKNOWN;
    
    /**
     * Indicates if the gate is currently in service
     * Inactive gates cannot be assigned regardless of status
     */
    @NotNull(message = "Active status must be specified")
    private Boolean isActive = true;
    
    /**
     * Indicates whether the gate has a jet bridge (passenger boarding bridge)
     * Gates without jet bridges require stairs or ramps for boarding
     */
    private boolean hasJetBridge = true;
    
    /**
     * List of additional features/facilities available at this gate
     * Affects compatibility with certain aircraft types and operations
     */
    private List<GateFeature> features = new ArrayList<>();
    
    /**
     * Maximum passenger capacity that can be handled at this gate
     * Used for planning and allocation purposes
     */
    @NotNull(message = "Capacity must be specified")
    private int capacity;
    
    /**
     * Audit timestamps for tracking gate record creation and updates
     */
    private LocalDateTime createdAt;  // When the gate was added to the system
    private LocalDateTime updatedAt;  // When the gate was last modified

    /**
     * Gate types representing the kind of flights the gate can handle.
     * 
     * This enum differentiates between:
     * - DOMESTIC: Gates that can only handle domestic flights
     * - INTERNATIONAL: Gates that can only handle international flights 
     * - BOTH: Gates that can handle both domestic and international flights
     * 
     * International gates typically require additional facilities for customs
     * and immigration processing.
     */
    public enum GateType {
        DOMESTIC("Domestic Flights"),
        INTERNATIONAL("International Flights"),
        BOTH("Both Domestic and International");

        private final String description;
        
        /**
         * Constructor for GateType enum
         * 
         * @param description Human-readable description of the gate type
         */
        GateType(String description) {
            this.description = description;
        }
        
        /**
         * Gets the human-readable description of the gate type
         * 
         * @return String description of the gate type
         */
        public String getDescription() {
            return description;
        }
    }

    /**
     * Gate sizes determining aircraft compatibility.
     * 
     * This enum defines the physical size categories of gates:
     * - SMALL: Can only accommodate regional/small aircraft
     * - MEDIUM: Can accommodate narrow-body aircraft (like Boeing 737, Airbus A320)
     * - LARGE: Can accommodate wide-body aircraft (like Boeing 777, Airbus A380)
     * 
     * The gate size is a critical factor in determining which aircraft types
     * can be assigned to a particular gate.
     */
    public enum GateSize {
        SMALL("Regional aircraft only"),
        MEDIUM("Up to narrow-body aircraft"),
        LARGE("Up to wide-body aircraft");

        private final String description;

        /**
         * Constructor for GateSize enum
         * 
         * @param description Human-readable description of the gate size
         */
        GateSize(String description) {
            this.description = description;
        }

        /**
         * Gets the human-readable description of the gate size
         * 
         * @return String description of what aircraft types the gate can handle
         */
        public String getDescription() {
            return description;
        }
    }

    /**
     * Gate features representing available facilities and capabilities.
     * 
     * Each feature provides specific functionality that may be required
     * for certain aircraft types or flight operations:
     * 
     * - JETBRIDGE: Enclosed, movable connector that extends from airport terminal to aircraft
     * - FUEL_PIT: In-ground fueling system for faster aircraft refueling
     * - POWER_SUPPLY: Ground power units to provide electricity to parked aircraft
     * - PRECONDITIONED_AIR: System to provide temperature-controlled air to aircraft
     * - WIDE_BODY_CAPABLE: Structural support and space for wide-body aircraft
     * - INTERNATIONAL_CAPABLE: Security and customs facilities for international flights
     */
    public enum GateFeature {
        JETBRIDGE("Passenger boarding bridge"),
        FUEL_PIT("In-ground fueling system"),
        POWER_SUPPLY("Ground power unit"),
        PRECONDITIONED_AIR("Aircraft cooling/heating system"),
        WIDE_BODY_CAPABLE("Can accommodate wide-body aircraft"),
        INTERNATIONAL_CAPABLE("Has customs and immigration access");

        private final String description;

        /**
         * Constructor for GateFeature enum
         * 
         * @param description Human-readable description of the feature
         */
        GateFeature(String description) {
            this.description = description;
        }

        /**
         * Gets the human-readable description of the gate feature
         * 
         * @return String description of the feature
         */
        public String getDescription() {
            return description;
        }
    }

    /**
     * Determines whether this gate is compatible with a specific aircraft type.
     * 
     * This method evaluates compatibility based on:
     * 1. Gate operational status (must be active and available)
     * 2. Size compatibility (gate must be large enough for the aircraft)
     * 3. Feature requirements (gate must have all required features for the aircraft)
     * 
     * This is a key business logic method used in gate assignment algorithms
     * to ensure that flights are only assigned to compatible gates.
     * 
     * @param aircraftType The aircraft type to check compatibility with
     * @return true if the gate can accommodate the aircraft type, false otherwise
     */
    public boolean isCompatibleWith(AircraftType aircraftType) {
        // Check operational status - gate must be active and available
        if (!isActive || status != GateStatus.AVAILABLE) {
            return false;
        }

        // Check size compatibility based on aircraft type
        boolean sizeCompatible = switch(aircraftType) {
            case WIDE_BODY -> gateSize == GateSize.LARGE;
            case NARROW_BODY -> gateSize == GateSize.LARGE || gateSize == GateSize.MEDIUM;
            case REGIONAL_JET -> true; // Any gate size can handle regional jets
        };

        // Check if the gate has all the required features for this aircraft type
        return sizeCompatible && hasRequiredFeatures(aircraftType);
    }

    /**
     * Helper method to check if the gate has all required features for a specific aircraft type.
     * 
     * Different aircraft types require different gate features:
     * - All aircraft types require power supply
     * - Aircraft using jet bridge require the JETBRIDGE feature
     * - Wide-body aircraft require WIDE_BODY_CAPABLE and FUEL_PIT features
     * - Narrow-body aircraft require the FUEL_PIT feature
     * - Regional jets have minimal feature requirements
     * 
     * @param aircraftType The aircraft type to check feature requirements for
     * @return true if the gate has all required features, false otherwise
     */
    private boolean hasRequiredFeatures(AircraftType aircraftType) {
        // Start with basic requirements common to all aircraft
        List<GateFeature> requiredFeatures = new ArrayList<>();
        requiredFeatures.add(GateFeature.POWER_SUPPLY);
        
        // Add jet bridge requirement if this gate uses jet bridges
        if (hasJetBridge) {
            requiredFeatures.add(GateFeature.JETBRIDGE);
        }

        // Add aircraft-specific requirements
        switch(aircraftType) {
            case WIDE_BODY -> {
                requiredFeatures.add(GateFeature.WIDE_BODY_CAPABLE);
                requiredFeatures.add(GateFeature.FUEL_PIT);
            }
            case NARROW_BODY -> requiredFeatures.add(GateFeature.FUEL_PIT);
            case REGIONAL_JET -> {}  // No additional requirements
        }

        // Check if the gate has all the required features
        return features.containsAll(requiredFeatures);
    }

    /**
     * Gets the maximum passenger capacity of this gate.
     * 
     * @return The gate's passenger capacity
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * Sets the maximum passenger capacity of this gate.
     * 
     * @param capacity The new capacity value
     */
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
}