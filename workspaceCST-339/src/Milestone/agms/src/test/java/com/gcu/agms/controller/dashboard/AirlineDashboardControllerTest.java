package com.gcu.agms.controller.dashboard;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import com.gcu.agms.model.flight.FlightModel;
import com.gcu.agms.model.gate.GateModel;
import com.gcu.agms.service.flight.FlightOperationsService;
import com.gcu.agms.service.gate.GateManagementService;
import com.gcu.agms.service.gate.GateOperationsService;

import jakarta.servlet.http.HttpSession;

@DisplayName("Airline Dashboard Controller Tests")
class AirlineDashboardControllerTest {

    private MockMvc mockMvc;
    
    @Mock
    private GateOperationsService gateOperationsService;
    
    @Mock
    private GateManagementService gateManagementService;
    
    @Mock
    private FlightOperationsService flightOperationsService;
    
    @Mock
    private HttpSession session;

    private AirlineDashboardController controller;
    private MockHttpSession mockSession;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        
        controller = new AirlineDashboardController(
            gateOperationsService, 
            gateManagementService,
            flightOperationsService);
        
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/views/");
        viewResolver.setSuffix(".html");

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                                .setViewResolvers(viewResolver)
                                .build();

        mockSession = new MockHttpSession();
        mockSession.setAttribute("userRole", "AIRLINE_STAFF");
    }

    @Test
    @DisplayName("Should show airline dashboard")
    void testShowDashboard() throws Exception {
        mockMvc.perform(get("/airline/dashboard").session(mockSession))
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
        gate.setId(Long.valueOf(gateId));
        
        when(gateManagementService.getGateById(gateId)).thenReturn(Optional.of(gate));
        
        mockMvc.perform(get("/airline/schedule/{gateId}", gateId).session(mockSession))
               .andExpect(status().isOk())
               .andExpect(view().name("airline/schedule"))
               .andExpect(model().attributeExists("gate"))
               .andExpect(model().attribute("pageTitle", "Gate Schedule - AGMS"));
    }

    @Test
    @DisplayName("Should show alerts")
    void testShowAlerts() throws Exception {
        mockMvc.perform(get("/airline/alerts").session(mockSession))
               .andExpect(status().isOk())
               .andExpect(view().name("airline/alerts"))
               .andExpect(model().attribute("pageTitle", "Operational Alerts - AGMS"));
    }

    @Test
    @DisplayName("Should show airline dashboard with authorized role")
    void testShowDashboardAuthorized() throws Exception {
        // Setup mock data
        when(session.getAttribute("userRole")).thenReturn("AIRLINE_STAFF");
        
        // Mock statistics
        Map<String, Integer> stats = new HashMap<>();
        stats.put("totalFlights", 10);
        when(flightOperationsService.getOperationalStatistics()).thenReturn(stats);
        
        // Mock active flights with correct return type
        Map<String, Object> flightData = new HashMap<>();
        flightData.put("flightNumber", "AA123");
        flightData.put("status", "EN_ROUTE");
        List<Map<String, Object>> activeFlights = Arrays.asList(flightData);
        when(flightOperationsService.getActiveFlights()).thenReturn(activeFlights);

        // Perform request and verify
        mockMvc.perform(get("/airline/dashboard").sessionAttr("userRole", "AIRLINE_STAFF"))
               .andExpect(status().isOk())
               .andExpect(view().name("dashboard/airline"))
               .andExpect(model().attributeExists("statistics"))
               .andExpect(model().attributeExists("activeFlights"));
    }

    @Test
    @DisplayName("Should redirect unauthorized access to login")
    void testShowDashboardUnauthorized() throws Exception {
        mockMvc.perform(get("/airline/dashboard").sessionAttr("userRole", "PUBLIC"))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/login"));
    }

    @Test
    @DisplayName("Should handle flight status updates")
    void testUpdateFlightStatus() throws Exception {
        when(flightOperationsService.updateFlightStatus("AA123", "DELAYED", "Gate 1"))
            .thenReturn(true);

        mockMvc.perform(post("/airline/flights/AA123/status")
                .session(mockSession)  // Add session
                .param("newStatus", "DELAYED")
                .param("location", "Gate 1"))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/airline/dashboard"))
               .andExpect(flash().attributeExists("success"));
    }

    @Test
    @DisplayName("Should handle failed flight status updates")
    void testUpdateFlightStatusFailure() throws Exception {
        when(flightOperationsService.updateFlightStatus("AA123", "DELAYED", "Gate 1"))
            .thenReturn(false);

        mockMvc.perform(post("/airline/flights/AA123/status")
                .session(mockSession)  // Add session
                .param("newStatus", "DELAYED")
                .param("location", "Gate 1"))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/airline/dashboard"))
               .andExpect(flash().attributeExists("error"));
    }

    @Test
    @DisplayName("Should show flight details")
    void testShowFlightDetails() throws Exception {
        // Setup mock data with full flight details
        Map<String, Object> flightDetails = new HashMap<>();
        FlightModel flight = new FlightModel();
        flight.setFlightNumber("AA123");
        flight.setAirlineCode("AA");
        flight.setOrigin("JFK");
        flight.setDestination("LAX");
        flightDetails.put("flight", flight);  // Changed key to "flight" to match controller expectation
        
        when(flightOperationsService.getFlightDetails("AA123")).thenReturn(flightDetails);

        // Perform request with session
        mockMvc.perform(get("/airline/flights/AA123")
                .session(mockSession))
               .andExpect(status().isOk())
               .andExpect(view().name("flight/details"))
               .andExpect(model().attributeExists("flight"))
               .andExpect(model().attribute("pageTitle", "Flight Details - AGMS"));

        // Verify service was called
        verify(flightOperationsService).getFlightDetails("AA123");
    }
}
