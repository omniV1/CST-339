package com.gcu.agms.controller.dashboard;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import com.gcu.agms.model.gate.GateModel;
import com.gcu.agms.service.flight.FlightOperationsService;
import com.gcu.agms.service.gate.GateManagementService;
import com.gcu.agms.service.gate.GateOperationsService;

@DisplayName("Airline Dashboard Controller Tests")
class AirlineDashboardControllerTest {

    private MockMvc mockMvc;
    private GateOperationsService gateOperationsService;
    private GateManagementService gateManagementService;
    private FlightOperationsService flightOperationsService;
    private AirlineDashboardController controller;
    private MockHttpSession session;

    @BeforeEach
    void setUp() {
        // Initialize mocks
        gateOperationsService = mock(GateOperationsService.class);
        gateManagementService = mock(GateManagementService.class);
        flightOperationsService = mock(FlightOperationsService.class);
        
        // Create controller with mocked services
        controller = new AirlineDashboardController(gateOperationsService, gateManagementService);
        
        // Configure view resolver
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/views/");
        viewResolver.setSuffix(".html");
        
        // Setup MockMvc
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                                .setViewResolvers(viewResolver)
                                .build();
        
        // Setup session
        session = new MockHttpSession();
        session.setAttribute("userRole", "AIRLINE_STAFF");

        // Setup default mock behaviors
        Map<String, Integer> stats = new HashMap<>();
        stats.put("totalGates", 10);
        stats.put("availableGates", 5);
        
        when(gateOperationsService.getStatistics()).thenReturn(stats);
        when(gateOperationsService.getAllGateStatuses()).thenReturn(new HashMap<>());
        when(gateManagementService.getAllGates()).thenReturn(Arrays.asList(new GateModel()));
    }

    @Test
    @DisplayName("Should show airline dashboard")
    void testShowDashboard() throws Exception {
        mockMvc.perform(get("/airline/dashboard").session(session))
               .andExpect(status().isOk())
               .andExpect(view().name("dashboard/airline"))
               .andExpect(model().attributeExists("gateStatuses"))
               .andExpect(model().attributeExists("statistics"))
               .andExpect(model().attribute("pageTitle", "Airline Staff Dashboard - AGMS"));
    }

    @Test
    @DisplayName("Should show gate schedule")
    void testShowGateSchedule() throws Exception {
        // Use String for gate ID
        String gateId = "1";
        GateModel gate = new GateModel();
        gate.setId(Long.parseLong(gateId));
        
        when(gateManagementService.getGateById(gateId)).thenReturn(Optional.of(gate));
        
        mockMvc.perform(get("/airline/schedule/{gateId}", gateId).session(session))
               .andExpect(status().isOk())
               .andExpect(view().name("airline/schedule"))
               .andExpect(model().attributeExists("gate"))
               .andExpect(model().attribute("pageTitle", "Gate Schedule - AGMS"));
    }

    @Test
    @DisplayName("Should show alerts")
    void testShowAlerts() throws Exception {
        mockMvc.perform(get("/airline/alerts").session(session))
               .andExpect(status().isOk())
               .andExpect(view().name("airline/alerts"))
               .andExpect(model().attribute("pageTitle", "Operational Alerts - AGMS"));
    }
}
