package com.gcu.agms.service.impl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.gcu.agms.model.gate.GateModel;

/**
 * Test suite for InMemoryGateManagementService
 * Tests CRUD operations for gate management
 */
@DisplayName("InMemoryGateManagement Service Tests")
class InMemoryGateManagementServiceTest {

    private InMemoryGateManagementService service;

    @BeforeEach
    public void setUp() {
        service = new InMemoryGateManagementService();
        service.initialize(); // Initialize sample data
    }

    /**
     * Verifies retrieval of all gates
     * Tests initialization and basic data access
     */
    @Test
    @DisplayName("Should return all gates")
    void testGetAllGates() {
        List<GateModel> gates = service.getAllGates();
        assertNotNull(gates);
        assertFalse(gates.isEmpty());
        assertEquals(20, gates.size()); // 4 terminals × 5 gates
    }

    /**
     * Tests gate retrieval by ID
     * Validates specific gate lookup functionality
     */
    @Test
    @DisplayName("Should find gate by ID")
    void testGetGateById() {
        Optional<GateModel> gate = service.getGateById("T1G1");
        assertTrue(gate.isPresent());
        assertEquals("T1G1", gate.get().getGateId());
        assertEquals("1", gate.get().getTerminal());
        assertEquals("1", gate.get().getGateNumber());
    }

    /**
     * Ensures proper handling of non-existent gates
     * Tests error handling for invalid gate IDs
     */
    @Test
    @DisplayName("Should return empty Optional for non-existent gate")
    void testGetNonExistentGate() {
        Optional<GateModel> gate = service.getGateById("NONEXISTENT");
        assertFalse(gate.isPresent());
    }

    /**
     * Validates filtering gates by terminal
     * Tests terminal-specific gate management
     */
    @Test
    @DisplayName("Should get gates by terminal")
    void testGetGatesByTerminal() {
        List<GateModel> terminalGates = service.getGatesByTerminal("1");
        assertNotNull(terminalGates);
        assertEquals(5, terminalGates.size());
        terminalGates.forEach(gate -> assertEquals("1", gate.getTerminal()));
    }

    /**
     * Tests gate creation functionality
     * Verifies new gate registration process
     */
    @Test
    @DisplayName("Should create new gate")
    void testCreateGate() {
        GateModel newGate = new GateModel();
        newGate.setGateId("T5G1");
        newGate.setTerminal("5");
        newGate.setGateNumber("1");
        
        assertTrue(service.createGate(newGate));
        
        Optional<GateModel> created = service.getGateById("T5G1");
        assertTrue(created.isPresent());
        assertEquals("5", created.get().getTerminal());
    }

    /**
     * Ensures duplicate gates cannot be created
     * Tests uniqueness constraints
     */
    @Test
    @DisplayName("Should not create duplicate gate")
    void testCreateDuplicateGate() {
        GateModel duplicateGate = new GateModel();
        duplicateGate.setGateId("T1G1");
        duplicateGate.setTerminal("1");
        duplicateGate.setGateNumber("1");
        
        assertFalse(service.createGate(duplicateGate));
    }

    /**
     * Validates gate update functionality
     * Tests modification of existing gate properties
     */
    @Test
    @DisplayName("Should update existing gate")
    void testUpdateGate() {
        GateModel updatedGate = new GateModel();
        updatedGate.setGateId("T1G1");
        updatedGate.setTerminal("1");
        updatedGate.setGateNumber("1");
        updatedGate.setCapacity(500);
        
        assertTrue(service.updateGate(updatedGate.getGateId(), updatedGate));
        
        Optional<GateModel> gate = service.getGateById("T1G1");
        assertTrue(gate.isPresent());
        assertEquals(500, gate.get().getCapacity());
    }
}