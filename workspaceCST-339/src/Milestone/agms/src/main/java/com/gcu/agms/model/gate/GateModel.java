package com.gcu.agms.model.gate;

import java.util.ArrayList;
import java.util.List;

import com.gcu.agms.model.flight.AircraftType;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * Represents a physical gate in the Airport Gate Management System (AGMS).
 * This model follows the UML class diagram specifications while incorporating
 * practical validation and operational requirements.
 */
/**
 * Represents a gate in an airport with various attributes and capabilities.
 * This model includes validation annotations for ensuring proper data integrity.
 * 
 * Attributes:
 * - id: Database identifier for the gate.
 * - gateId: Unique identifier for the gate, must follow the format T#G# (e.g., T1G1).
 * - terminal: Terminal number where the gate is located, must be between 1 and 4.
 * - gateNumber: Number of the gate, must be 1-2 digits.
 * - gateType: Type of flights the gate can handle (Domestic, International, Both).
 * - gateSize: Size of the gate determining aircraft compatibility (Small, Medium, Large).
 * - status: Current operational status of the gate.
 * - isActive: Indicates if the gate is currently active.
 * - hasJetBridge: Indicates if the gate has a jet bridge.
 * - features: List of features and capabilities available at the gate.
 * - capacity: Capacity of the gate.
 * 
 * Enums:
 * - GateType: Represents the kind of flights the gate can handle.
 * - GateSize: Determines aircraft compatibility based on gate size.
 * - GateFeature: Represents available facilities and features at the gate.
 * 
 * Methods:
 * - isCompatibleWith(AircraftType aircraftType): Checks if the gate is compatible with a specific aircraft type.
 * - hasRequiredFeatures(AircraftType aircraftType): Helper method to check if the gate has all required features for an aircraft type.
 * - getCapacity(): Returns the capacity of the gate.
 * - setCapacity(int capacity): Sets the capacity of the gate.
 */
@Data   
public class GateModel {
    // Database identifier
    private Long id;
    
    // Basic gate information with validation
    @NotEmpty(message = "Gate ID is required")
    @Pattern(regexp = "^T[1-4]G[0-9]{1,2}$", 
             message = "Gate ID must be in format T#G# (e.g., T1G1)")
    private String gateId;
    
    @NotEmpty(message = "Terminal number is required")
    @Pattern(regexp = "^[1-4]$", message = "Terminal must be between 1 and 4")
    private String terminal;
    
    @NotEmpty(message = "Gate number is required")
    @Pattern(regexp = "^[0-9]{1,2}$", message = "Gate number must be 1-2 digits")
    private String gateNumber;
    
    // Operational characteristics
    @NotNull(message = "Gate type must be specified")
    private GateType gateType = GateType.DOMESTIC;
    
    @NotNull(message = "Gate size must be specified")
    private GateSize gateSize = GateSize.MEDIUM;
    
    @NotNull(message = "Gate status must be specified")
    private GateStatus status = GateStatus.UNKNOWN;
    
    @NotNull(message = "Active status must be specified")
    private Boolean isActive = true;
    
    // Features and capabilities
    private boolean hasJetBridge = true;
    private List<GateFeature> features = new ArrayList<>();
    
    @NotNull(message = "Capacity must be specified")
    private int capacity;

    /**
     * Gate types representing the kind of flights the gate can handle
     */
    public enum GateType {
        DOMESTIC("Domestic Flights"),
        INTERNATIONAL("International Flights"),
        BOTH("Both Domestic and International");

        private final String description;
        
        GateType(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }

    /**
     * Gate sizes determining aircraft compatibility
     */
    public enum GateSize {
        SMALL("Regional aircraft only"),
        MEDIUM("Up to narrow-body aircraft"),
        LARGE("Up to wide-body aircraft");

        private final String description;

        GateSize(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * Gate features representing available facilities
     */
    public enum GateFeature {
        JETBRIDGE("Passenger boarding bridge"),
        FUEL_PIT("In-ground fueling system"),
        POWER_SUPPLY("Ground power unit"),
        PRECONDITIONED_AIR("Aircraft cooling/heating system"),
        WIDE_BODY_CAPABLE("Can accommodate wide-body aircraft"),
        INTERNATIONAL_CAPABLE("Has customs and immigration access");

        private final String description;

        GateFeature(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * Checks if the gate is compatible with a specific aircraft type
     * @param aircraftType the type of aircraft to check compatibility with
     * @return true if the gate can accommodate the aircraft type
     */
    public boolean isCompatibleWith(AircraftType aircraftType) {
        if (!isActive || status != GateStatus.AVAILABLE) {
            return false;
        }

        // Check size compatibility
        boolean sizeCompatible = switch(aircraftType) {
            case WIDE_BODY -> gateSize == GateSize.LARGE;
            case NARROW_BODY -> gateSize == GateSize.LARGE || gateSize == GateSize.MEDIUM;
            case REGIONAL_JET -> true; // Any gate size can handle regional jets
        };

        return sizeCompatible && hasRequiredFeatures(aircraftType);
    }

    /**
     * Helper method to check if the gate has all required features for an aircraft type
     */
    private boolean hasRequiredFeatures(AircraftType aircraftType) {
        List<GateFeature> requiredFeatures = new ArrayList<>();
        requiredFeatures.add(GateFeature.POWER_SUPPLY);
        
        if (hasJetBridge) {
            requiredFeatures.add(GateFeature.JETBRIDGE);
        }

        switch(aircraftType) {
            case WIDE_BODY -> {
                requiredFeatures.add(GateFeature.WIDE_BODY_CAPABLE);
                requiredFeatures.add(GateFeature.FUEL_PIT);
            }
            case NARROW_BODY -> requiredFeatures.add(GateFeature.FUEL_PIT);
            case REGIONAL_JET -> {} // No additional features required
        }

        return features.containsAll(requiredFeatures);
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
}