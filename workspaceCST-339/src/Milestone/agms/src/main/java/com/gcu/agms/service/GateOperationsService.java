package com.gcu.agms.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Service
public class GateOperationsService {
    // We store our gate statuses in a Map, where the key is the gate ID and the value is its status
    private final Map<String, GateStatus> gateStatuses = new HashMap<>();
    
    // This enum defines all possible states a gate can be in
    public enum GateStatus {
        AVAILABLE("Available", "success"),
        OCCUPIED("Occupied", "warning"),
        MAINTENANCE("Maintenance", "danger");
        
        private final String label;
        private final String cssClass;
        
        GateStatus(String label, String cssClass) {
            this.label = label;
            this.cssClass = cssClass;
        }
        
        public String getLabel() { return label; }
        public String getCssClass() { return cssClass; }
    }

    // This method runs when the service is created and sets up our initial gate data
    @PostConstruct
    public void initializeData() {
        // Create sample gates for each terminal
        for (int terminal = 1; terminal <= 4; terminal++) {
            for (int gate = 1; gate <= 5; gate++) {
                String gateId = String.format("T%dG%d", terminal, gate);
                // Randomly assign an initial status to each gate
                int random = new Random().nextInt(3);
                gateStatuses.put(gateId, GateStatus.values()[random]);
            }
        }
    }

    // These are the two methods our controller needs to call
    public Map<String, GateStatus> getAllGateStatuses() {
        return new HashMap<>(gateStatuses);
    }

    public Map<String, Integer> getStatistics() {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("totalGates", gateStatuses.size());
        stats.put("availableGates", countGatesByStatus(GateStatus.AVAILABLE));
        stats.put("occupiedGates", countGatesByStatus(GateStatus.OCCUPIED));
        stats.put("maintenanceGates", countGatesByStatus(GateStatus.MAINTENANCE));
        return stats;
    }

    // Helper method to count gates by their status
    private int countGatesByStatus(GateStatus status) {
        return (int) gateStatuses.values()
                .stream()
                .filter(s -> s == status)
                .count();
    }
}