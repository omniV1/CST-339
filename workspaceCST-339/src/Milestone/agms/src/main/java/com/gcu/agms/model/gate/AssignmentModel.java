package com.gcu.agms.model.gate;

import java.time.Clock;
import java.time.LocalDateTime;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a gate assignment in the Airport Gate Management System.
 * This model connects flights with gates for specific time periods and
 * tracks the status of each assignment.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentModel {
    private Long id;

    @NotBlank(message = "Gate ID is required")
    private String gateId;

    @NotBlank(message = "Flight number is required")
    private String flightNumber;

    @NotNull(message = "Start time is required")
    private LocalDateTime startTime;

    @NotNull(message = "End time is required")
    private LocalDateTime endTime;

    @NotNull(message = "Assignment status is required")
    @Builder.Default
    private AssignmentStatus status = AssignmentStatus.SCHEDULED;

    private String assignedBy;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    
    @Builder.Default
    private boolean cancelled = false;
    
    @Builder.Default
    private Clock clock = Clock.systemDefaultZone();

    /**
     * Checks if this assignment has a time conflict with another assignment
     */
    public boolean hasConflict(AssignmentModel other) {
        // Return false if either assignment is cancelled or is invalid
        if (this.cancelled || other == null || other.cancelled || 
            !isValid() || !other.isValid()) {
            return false;
        }
        
        // No conflict if one assignment ends before the other starts
        return !(this.endTime.isBefore(other.startTime) || 
                 this.startTime.isAfter(other.endTime));
    }

    /**
     * Checks if this assignment is currently active
     */
    public boolean isActive() {
        if (!isValid()) {
            return false;
        }
        LocalDateTime now = LocalDateTime.now(clock);
        return !cancelled && startTime.isBefore(now) && endTime.isAfter(now);
    }

    /**
     * Checks if this assignment is cancelled
     */
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * Updates the status of this assignment
     */
    public void updateStatus(AssignmentStatus newStatus) {
        this.status = newStatus;
        this.updatedAt = LocalDateTime.now(clock);
    }

    /**
     * Initializes timestamps for a new assignment
     */
    public void initializeTimestamps() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now(clock);
        }
        this.updatedAt = LocalDateTime.now(clock);
    }

    /**
     * Validates that all required fields are present
     */
    private boolean isValid() {
        return startTime != null && endTime != null;
    }
    
    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
}