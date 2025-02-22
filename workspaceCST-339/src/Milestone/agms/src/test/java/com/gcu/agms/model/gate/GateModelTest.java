package com.gcu.agms.model.gate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.gcu.agms.model.flight.AircraftType;
import com.gcu.agms.model.gate.GateModel.GateFeature;
import com.gcu.agms.model.gate.GateModel.GateSize;
import com.gcu.agms.model.gate.GateModel.GateType;

@DisplayName("Gate Model Tests")
class GateModelTest {

    @Test
    @DisplayName("Should create gate with all properties") 
    void testGateCreation() {
        GateModel gate = new GateModel();
        
        // Test all setters
        gate.setId(1L);
        gate.setGateId("T1G1");
        gate.setTerminal("1"); 
        gate.setGateNumber("1");
        gate.setGateType(GateType.DOMESTIC);
        gate.setGateSize(GateSize.MEDIUM);
        gate.setStatus(GateStatus.AVAILABLE);
        gate.setIsActive(true);
        gate.setHasJetBridge(true);
        gate.setCapacity(100);

        List<GateFeature> features = new ArrayList<>();
        features.add(GateFeature.POWER_SUPPLY);
        features.add(GateFeature.FUEL_PIT);
        gate.setFeatures(features);

        // Test all getters
        assertEquals(1L, gate.getId());
        assertEquals("T1G1", gate.getGateId());
        assertEquals("1", gate.getTerminal());
        assertEquals("1", gate.getGateNumber());
        assertEquals(GateType.DOMESTIC, gate.getGateType());
        assertEquals(GateSize.MEDIUM, gate.getGateSize());
        assertEquals(GateStatus.AVAILABLE, gate.getStatus());
        assertTrue(gate.getIsActive());
        assertTrue(gate.isHasJetBridge());
        assertEquals(100, gate.getCapacity());
        assertEquals(2, gate.getFeatures().size());
    }

    @Test
    @DisplayName("Should test enum values and descriptions")
    void testEnumDescriptions() {
        // Test all GateType values
        assertAll("GateType enum values",
            () -> assertNotNull(GateType.values()),
            () -> assertEquals(3, GateType.values().length),
            () -> assertEquals("Domestic Flights", GateType.DOMESTIC.getDescription()),
            () -> assertEquals("International Flights", GateType.INTERNATIONAL.getDescription()),
            () -> assertEquals("Both Domestic and International", GateType.BOTH.getDescription())
        );

        // Test all GateSize values
        assertAll("GateSize enum values",
            () -> assertNotNull(GateSize.values()),
            () -> assertEquals(3, GateSize.values().length),
            () -> assertEquals("Regional aircraft only", GateSize.SMALL.getDescription()),
            () -> assertEquals("Up to narrow-body aircraft", GateSize.MEDIUM.getDescription()),
            () -> assertEquals("Up to wide-body aircraft", GateSize.LARGE.getDescription())
        );

        // Test all GateFeature values
        assertAll("GateFeature enum values",
            () -> assertNotNull(GateFeature.values()),
            () -> assertEquals(6, GateFeature.values().length),
            () -> assertEquals("Passenger boarding bridge", GateFeature.JETBRIDGE.getDescription()),
            () -> assertEquals("In-ground fueling system", GateFeature.FUEL_PIT.getDescription()),
            () -> assertEquals("Ground power unit", GateFeature.POWER_SUPPLY.getDescription()),
            () -> assertEquals("Aircraft cooling/heating system", GateFeature.PRECONDITIONED_AIR.getDescription()),
            () -> assertEquals("Can accommodate wide-body aircraft", GateFeature.WIDE_BODY_CAPABLE.getDescription()),
            () -> assertEquals("Has customs and immigration access", GateFeature.INTERNATIONAL_CAPABLE.getDescription())
        );
    }

    @Test
    @DisplayName("Should test aircraft compatibility with features")
    void testAircraftCompatibility() {
        GateModel gate = new GateModel();
        gate.setIsActive(true);
        gate.setStatus(GateStatus.AVAILABLE);
        
        // Test LARGE gate size with all required features for wide-body
        gate.setGateSize(GateSize.LARGE);
        gate.setHasJetBridge(true);
        List<GateFeature> wideBodyFeatures = Arrays.asList(
            GateFeature.POWER_SUPPLY,
            GateFeature.FUEL_PIT,
            GateFeature.WIDE_BODY_CAPABLE,
            GateFeature.JETBRIDGE
        );
        gate.setFeatures(wideBodyFeatures);
        
        assertTrue(gate.isCompatibleWith(AircraftType.WIDE_BODY), "Large gate should accept wide-body aircraft");
        assertTrue(gate.isCompatibleWith(AircraftType.NARROW_BODY), "Large gate should accept narrow-body aircraft");
        assertTrue(gate.isCompatibleWith(AircraftType.REGIONAL_JET), "Large gate should accept regional jets");

        // Test MEDIUM gate size with narrow-body features
        gate.setGateSize(GateSize.MEDIUM);
        List<GateFeature> narrowBodyFeatures = Arrays.asList(
            GateFeature.POWER_SUPPLY,
            GateFeature.FUEL_PIT,
            GateFeature.JETBRIDGE
        );
        gate.setFeatures(narrowBodyFeatures);
        
        assertFalse(gate.isCompatibleWith(AircraftType.WIDE_BODY), "Medium gate should reject wide-body aircraft");
        assertTrue(gate.isCompatibleWith(AircraftType.NARROW_BODY), "Medium gate should accept narrow-body aircraft");
        assertTrue(gate.isCompatibleWith(AircraftType.REGIONAL_JET), "Medium gate should accept regional jets");

        // Test SMALL gate size with minimal features
        gate.setGateSize(GateSize.SMALL);
        List<GateFeature> basicFeatures = Arrays.asList(
            GateFeature.POWER_SUPPLY,
            GateFeature.JETBRIDGE
        );
        gate.setFeatures(basicFeatures);
        
        assertFalse(gate.isCompatibleWith(AircraftType.WIDE_BODY), "Small gate should reject wide-body aircraft");
        assertFalse(gate.isCompatibleWith(AircraftType.NARROW_BODY), "Small gate should reject narrow-body aircraft");
        assertTrue(gate.isCompatibleWith(AircraftType.REGIONAL_JET), "Small gate should accept regional jets");
    }

    @Test
    @DisplayName("Should test gate status conditions")
    void testGateStatusConditions() {
        GateModel gate = new GateModel();
        gate.setGateSize(GateSize.LARGE);
        gate.setHasJetBridge(true);
        List<GateFeature> features = Arrays.asList(
            GateFeature.POWER_SUPPLY,
            GateFeature.FUEL_PIT,
            GateFeature.WIDE_BODY_CAPABLE,
            GateFeature.JETBRIDGE
        );
        gate.setFeatures(features);

        // Test inactive gate
        gate.setIsActive(false);
        gate.setStatus(GateStatus.AVAILABLE);
        assertFalse(gate.isCompatibleWith(AircraftType.WIDE_BODY), 
            "Inactive gate should not accept aircraft");

        // Test maintenance status
        gate.setIsActive(true);
        gate.setStatus(GateStatus.MAINTENANCE);
        assertFalse(gate.isCompatibleWith(AircraftType.WIDE_BODY), 
            "Gate under maintenance should not accept aircraft");

        // Test closed status
        gate.setStatus(GateStatus.CLOSED);
        assertFalse(gate.isCompatibleWith(AircraftType.WIDE_BODY), 
            "Closed gate should not accept aircraft");

        // Test available status
        gate.setStatus(GateStatus.AVAILABLE);
        assertTrue(gate.isCompatibleWith(AircraftType.WIDE_BODY), 
            "Available gate should accept compatible aircraft");
    }

    @Test
    @DisplayName("Should validate gate ID format")
    void testGateIdValidation() {
        GateModel gate = new GateModel();
        
        assertAll("Gate ID validation",
            () -> {
                gate.setGateId("T1G1");
                assertEquals("T1G1", gate.getGateId());
            },
            () -> {
                gate.setGateId("T4G12");
                assertEquals("T4G12", gate.getGateId());
            }
        );
    }
}
