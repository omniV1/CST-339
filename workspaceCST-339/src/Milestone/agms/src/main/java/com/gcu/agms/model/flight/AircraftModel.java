package com.gcu.agms.model.flight;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents an aircraft in the Airline Ground Management System (AGMS).
 * This model class contains essential information about aircraft including
 * registration details, operational status, and maintenance scheduling.
 *
 * @Component This class is a Spring component for dependency injection
 *
 * @property registrationNumber The unique registration identifier of the aircraft
 * @property model The specific model name/number of the aircraft
 * @property type The category/type of the aircraft
 * @property status Current operational status of the aircraft (default: AVAILABLE)
 * @property currentLocation The current location/station of the aircraft
 * @property nextMaintenanceDue Scheduled date and time for next maintenance
 *
 */
@Getter
@Setter
@NoArgsConstructor
@Component
public class AircraftModel {
    @NotEmpty(message = "Registration number is required")
    private String registrationNumber;

    @NotEmpty(message = "Aircraft model is required")
    private String model;

    @NotNull(message = "Aircraft type must be specified")
    private AircraftType type;

    private AircraftStatus status = AircraftStatus.AVAILABLE;
    private String currentLocation;

    private LocalDateTime nextMaintenanceDue;

    public AircraftModel(String registrationNumber, String model, AircraftType type) {
        this.registrationNumber = registrationNumber;
        this.model = model;
        this.type = type;
        this.status = AircraftStatus.AVAILABLE;
    }

    public boolean isAvailableForService() {
        return status == AircraftStatus.AVAILABLE;
    }
    private Long id;

    public enum AircraftStatus {
        AVAILABLE("Available for service", "success"),
        IN_SERVICE("Currently in service", "primary"),
        MAINTENANCE("Under maintenance", "warning"),
        GROUNDED("Aircraft is grounded", "danger"),
        ACTIVE("Active", "success");

        private final String description;
        private final String cssClass;

        AircraftStatus(String description, String cssClass) {
            this.description = description;
            this.cssClass = cssClass;
        }

        public String getDescription() {
            return description;
        }

        public String getCssClass() {
            return cssClass;
        }
    }
    /**
 * Getter for the database ID
 * @return The database ID
 */
public Long getId() {
    return id;
}

/**
 * Setter for the database ID
 * @param id The database ID to set
 */
public void setId(Long id) {
    this.id = id;
}
}