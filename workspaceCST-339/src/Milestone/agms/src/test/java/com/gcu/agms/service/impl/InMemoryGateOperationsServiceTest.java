package com.gcu.agms.service.impl;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.gcu.agms.service.gate.GateOperationsService.GateStatus;

@DisplayName("InMemoryGateOperationsService Tests")
class InMemoryGateOperationsServiceTest {
    
    private InMemoryGateOperationsService gateOperationsService;

    @BeforeEach
    void setUp() {
        gateOperationsService = new InMemoryGateOperationsService();
        gateOperationsService.initializeData();
    }

    @Test
    @DisplayName("Should retrieve all gate statuses")
    void testGetAllGateStatuses() {
        Map<String, GateStatus> statuses = gateOperationsService.getAllGateStatuses();
        assertNotNull(statuses, "Gate statuses should not be null");
        assertFalse(statuses.isEmpty(), "Gate statuses should not be empty");
    }

    @Test
    @DisplayName("Should retrieve valid statistics")
    void testGetStatistics() {
        Map<String, Integer> stats = gateOperationsService.getStatistics();
        assertNotNull(stats, "Statistics should not be null");
        assertTrue(stats.containsKey("totalGates"), "Should contain total gates count");
        assertTrue(stats.containsKey("availableGates"), "Should contain available gates count");
        assertTrue(stats.containsKey("occupiedGates"), "Should contain occupied gates count");
        assertTrue(stats.containsKey("maintenanceGates"), "Should contain maintenance gates count");
    }

    @Test
    @DisplayName("Should count gates by status correctly")
    void testCountGatesByStatus() {
        Map<String, GateStatus> statuses = gateOperationsService.getAllGateStatuses();
        int availableCount = (int) statuses.values().stream()
            .filter(status -> status == GateStatus.AVAILABLE)
            .count();
        
        Map<String, Integer> stats = gateOperationsService.getStatistics();
        assertEquals(availableCount, stats.get("availableGates"), 
            "Available gates count should match");
    }
}