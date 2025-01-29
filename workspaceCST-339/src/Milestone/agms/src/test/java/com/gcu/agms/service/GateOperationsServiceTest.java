package com.gcu.agms.service;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Test class for GateOperationsService
 * This class tests the functionality related to gate management and status tracking
 * in the Airport Gate Management System.
 */
@DisplayName("Gate Operations Service Tests")
public class GateOperationsServiceTest {
    private GateOperationsService gateService;

    /**
     * Sets up a new GateOperationsService instance and initializes test data before each test.
     * This ensures each test starts with a known state of gate assignments and statuses.
     */
    @BeforeEach
    @DisplayName("Initialize Gate Operations Service")
    void setUp() {
        gateService = new GateOperationsService();
        gateService.initializeData();
    }

    /**
     * Tests retrieval of all gate statuses.
     * Verifies that the correct number of gates exists and that the status map is properly populated.
     */
    @Test
    @DisplayName("Should retrieve all gate statuses correctly")
    void testGetAllGateStatuses() {
        Map<String, GateOperationsService.GateStatus> statuses = gateService.getAllGateStatuses();
        
        // Verify the status map exists and is populated
        assertNotNull(statuses, "Gate statuses should not be null");
        assertFalse(statuses.isEmpty(), "Gate statuses should not be empty");
        
        // Verify we have the expected number of gates (4 terminals * 5 gates per terminal)
        assertEquals(20, statuses.size(), 
            "Should have exactly 20 gates (4 terminals * 5 gates)");
        
        // Verify gate naming convention
        assertTrue(statuses.containsKey("T1G1"), 
            "Should contain gate T1G1 (Terminal 1, Gate 1)");
        assertTrue(statuses.containsKey("T4G5"), 
            "Should contain gate T4G5 (Terminal 4, Gate 5)");
    }

    /**
     * Tests the statistics calculation functionality.
     * Verifies that gate counts are accurate and that all gates are accounted for.
     */
    @Test
    @DisplayName("Should calculate gate statistics correctly")
    void testGetStatistics() {
        Map<String, Integer> stats = gateService.getStatistics();
        
        // Verify statistics map exists and contains expected values
        assertNotNull(stats, "Statistics should not be null");
        assertEquals(20, stats.get("totalGates"), 
            "Total gates should be 20");
        
        // Verify individual counters are valid
        assertTrue(stats.get("availableGates") >= 0, 
            "Available gates should not be negative");
        assertTrue(stats.get("occupiedGates") >= 0, 
            "Occupied gates should not be negative");
        assertTrue(stats.get("maintenanceGates") >= 0, 
            "Maintenance gates should not be negative");
        
        // Verify total count matches sum of individual statuses
        assertEquals(20, 
            stats.get("availableGates") + 
            stats.get("occupiedGates") + 
            stats.get("maintenanceGates"),
            "Sum of gate statuses should equal total gates");
    }

    /**
     * Tests that gate status values contain valid labels and CSS classes.
     * Verifies that each status has the required display information.
     */
    @Test
    @DisplayName("Should have valid status display information")
    void testGateStatusValues() {
        Map<String, GateOperationsService.GateStatus> statuses = gateService.getAllGateStatuses();
        
        // Test each status has required display information
        for (GateOperationsService.GateStatus status : statuses.values()) {
            assertNotNull(status.getLabel(), 
                "Each status should have a display label");
            assertNotNull(status.getCssClass(), 
                "Each status should have a CSS class");
            assertFalse(status.getLabel().isEmpty(), 
                "Status label should not be empty");
            assertFalse(status.getCssClass().isEmpty(), 
                "CSS class should not be empty");
        }
    }
}