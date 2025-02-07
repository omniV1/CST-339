package com.gcu.agms.model.maintenance;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * Represents a maintenance record for aircraft maintenance tracking.
 * This class stores information about scheduled and completed maintenance activities.
 *
 */
@Data
public class MaintenanceRecord {
    private String recordId;
    private String registrationNumber;  // Aircraft registration number
    private LocalDateTime scheduledDate;
    private MaintenanceType type;
    private MaintenanceStatus status;
    private String technician;
    private String description;
    private LocalDateTime completionDate;
    private String notes;

    public enum MaintenanceType {
        ROUTINE("Routine Maintenance"),
        INSPECTION("Safety Inspection"),
        REPAIR("Repair Work"),
        OVERHAUL("Major Overhaul");

        private final String label;

        MaintenanceType(String label) {
            this.label = label;
        }

        public String getLabel() { return label; }
    }

    public enum MaintenanceStatus {
        SCHEDULED("Scheduled", "warning"),
        IN_PROGRESS("In Progress", "info"),
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