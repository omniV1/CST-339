package com.gcu.agms.model;

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

    /**
     * Checks if this assignment conflicts with another assignment's time window
     */
    public boolean hasConflict(AssignmentModel other) {
        if (!this.gateId.equals(other.gateId)) {
            return false;
        }
        return !(this.endTime.isBefore(other.startTime) || 
                this.startTime.isAfter(other.endTime));
    }

    /**
     * Updates the time window of this assignment
     */
    public boolean updateTimes(LocalDateTime newStart, LocalDateTime newEnd) {
        if (newEnd.isBefore(newStart)) {
            return false;
        }
        this.startTime = newStart;
        this.endTime = newEnd;
        this.updatedAt = LocalDateTime.now();
        return true;
    }

    /**
     * Checks if this assignment is currently active
     */
    public boolean isActive() {
        LocalDateTime now = LocalDateTime.now();
        return status == AssignmentStatus.ACTIVE && 
               now.isAfter(startTime) && 
               now.isBefore(endTime);
    }

    /**
     * Checks if the assignment has been completed
     */
    public boolean isComplete() {
        return status == AssignmentStatus.COMPLETED;
    }

    /**
     * Checks if the assignment has been cancelled
     */
    public boolean isCancelled() {
        return status == AssignmentStatus.CANCELLED;
    }

    /**
     * Updates the status of this assignment
     */
    public void updateStatus(AssignmentStatus newStatus) {
        this.status = newStatus;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Initializes timestamps when creating a new assignment
     */
    public void initializeTimestamps() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }
}