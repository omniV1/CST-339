package com.gcu.agms.model.gate;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Represents a gate assignment in the Airport Gate Management System.
 * This model connects flights with gates for specific time periods and
 * tracks the status of each assignment.
 */
@Data
public class AssignmentModel {
    private Long id;

    @NotEmpty(message = "Gate ID is required")
    private String gateId;

    @NotEmpty(message = "Flight number is required")
    private String flightNumber;

    @NotNull(message = "Start time is required")
    private LocalDateTime startTime;

    @NotNull(message = "End time is required")
    private LocalDateTime endTime;

    @NotNull(message = "Assignment status is required")
    private AssignmentStatus status = AssignmentStatus.SCHEDULED;

    private String assignedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean cancelled = false;

    /**
     * Checks if this assignment has a time conflict with another assignment
     */
    public boolean hasConflict(AssignmentModel other) {
        return !this.cancelled && 
               !other.cancelled &&
               !(this.endTime.isBefore(other.startTime) || 
                 this.startTime.isAfter(other.endTime));
    }

    /**
     * Checks if this assignment is currently active
     */
    public boolean isActive() {
        LocalDateTime now = LocalDateTime.now();
        return !cancelled && 
               now.isAfter(startTime) && 
               now.isBefore(endTime);
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
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Initializes timestamps for a new assignment
     */
    public void initializeTimestamps() {
        LocalDateTime now = LocalDateTime.now();
        if (this.createdAt == null) {
            this.createdAt = now;
        }
        this.updatedAt = now;
    }
}