package com.gcu.agms.model.common;

/**
 * Common interface for status enums.
 * This interface defines the common methods that all status enums should implement,
 * providing a consistent way to access status properties across different domains.
 */
public interface Status {
    /**
     * Gets the human-readable label for the status.
     * 
     * @return The status label
     */
    String getLabel();
    
    /**
     * Gets the CSS class associated with the status for UI styling.
     * 
     * @return The CSS class name
     */
    String getCssClass();
    
    /**
     * Gets the description of the status.
     * 
     * @return The status description
     */
    String getDescription();
}