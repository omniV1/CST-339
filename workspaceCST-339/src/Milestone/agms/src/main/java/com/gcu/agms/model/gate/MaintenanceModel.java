package com.gcu.agms.model.gate;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Represents a maintenance record for aircraft in the system.
 * This class contains details about maintenance tasks including scheduling,
 * type of maintenance, and current status.
 *
 * @Data annotation from Lombok provides getters, setters, toString, equals, and hashCode methods
 *
 * Properties:
 * - id: Unique identifier for the maintenance record
 * - aircraftRegistration: Registration number of the aircraft requiring maintenance
 * - startTime: Scheduled start time of maintenance
 * - endTime: Expected completion time of maintenance
 * - type: Type of maintenance (ROUTINE, REPAIR, INSPECTION, UPGRADE)
 * - description: Detailed description of the maintenance task
 * - location: Physical location where maintenance will be performed
 * - status: Current status of the maintenance task (SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED)
 * - technician: Name or ID of the assigned maintenance technician
 *
 * @see MaintenanceType
 * @see MaintenanceStatus
 */
@Data
public class MaintenanceModel {
    private Long id;

    @NotEmpty(message = "Aircraft registration is required")
    private String aircraftRegistration;

    @NotNull(message = "Start time is required")
    private LocalDateTime startTime;

    @NotNull(message = "End time is required")
    private LocalDateTime endTime;

    private MaintenanceType type;
    private String description;
    private String location;        // Where maintenance will be performed
    private MaintenanceStatus status = MaintenanceStatus.SCHEDULED;
    private String technician;      // Assigned maintenance technician

    public enum MaintenanceType {
        ROUTINE("Routine maintenance check"),
        REPAIR("Repair work"),
        INSPECTION("Safety inspection"),
        UPGRADE("System upgrade");

        private final String description;

        MaintenanceType(String description) {
            this.description = description;
        }

        public String getDescription() { return description; }
    }

    public enum MaintenanceStatus {
        SCHEDULED("Scheduled", "info"),
        IN_PROGRESS("In Progress", "warning"),
        COMPLETED("Completed", "success"),
        CANCELLED("Cancelled", "danger");

        private final String label;
        private final String cssClass;

        MaintenanceStatus(String label, String cssClass) {
            this.label = label;
            this.cssClass = cssClass;
        }

        public String getLabel() { return label; }
        public String getCssClass() { return cssClass; }
    }
}