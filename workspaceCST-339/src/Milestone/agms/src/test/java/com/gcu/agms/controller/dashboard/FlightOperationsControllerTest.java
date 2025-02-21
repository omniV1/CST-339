package com.gcu.agms.controller.dashboard;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


import com.gcu.agms.model.flight.AircraftModel;
import com.gcu.agms.model.flight.FlightModel;
import com.gcu.agms.service.flight.FlightOperationsService;

 class FlightOperationsControllerTest {

    private MockMvc mockMvc;

    @Mock
    private FlightOperationsService flightOperationsService;

    @InjectMocks
    private FlightOperationsController flightOperationsController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(flightOperationsController)
            .setControllerAdvice() // Add global exception handler if any
            .build();
    }

    @Test
    void testCreateFlight() throws Exception {
        FlightModel flight = new FlightModel();
        flight.setFlightNumber("AA123");
        flight.setAirlineCode("AA");
        flight.setOrigin("JFK");
        flight.setDestination("LAX");
        flight.setScheduledDeparture(LocalDateTime.now().plusDays(1));
        flight.setScheduledArrival(LocalDateTime.now().plusDays(1).plusHours(5));
        flight.setStatus(FlightModel.FlightStatus.SCHEDULED);

        when(flightOperationsService.updateFlight(any(FlightModel.class))).thenReturn(true);

        mockMvc.perform(post("/operations/flights/create")
                .contentType("application/json")
                .characterEncoding("UTF-8")
                .content("{" +
                    "\"flightNumber\":\"AA123\"," +
                    "\"airlineCode\":\"AA\"," +
                    "\"origin\":\"JFK\"," +
                    "\"destination\":\"LAX\"," +
                    "\"scheduledDeparture\":\"2025-02-08T20:00:00.000\"," +
                    "\"scheduledArrival\":\"2025-02-09T01:00:00.000\"," +
                    "\"status\":\"SCHEDULED\"" +
                    "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(flightOperationsService, times(1)).updateFlight(any(FlightModel.class));
    }

    @Test
    void testUpdateFlightStatus() throws Exception {
        when(flightOperationsService.updateFlightStatus(anyString(), anyString(), anyString())).thenReturn(true);

        mockMvc.perform(post("/operations/flights/status")
                .param("flightNumber", "AA123")
                .param("status", "DEPARTED")
                .param("location", "JFK"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(flightOperationsService, times(1)).updateFlightStatus("AA123", "DEPARTED", "JFK");
    }

    @Test
    void testScheduleMaintenance() throws Exception {
        when(flightOperationsService.scheduleMaintenance(anyString(), any(LocalDateTime.class), anyString(), anyString())).thenReturn(true);

        mockMvc.perform(post("/operations/aircraft/maintenance")
                .contentType("application/x-www-form-urlencoded")
                .param("registrationNumber", "N12345")
                .param("maintenanceDate", "2025-02-08 20:00:00")
                .param("maintenanceType", "ENGINE_CHECK")
                .param("description", "Routine engine check"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(flightOperationsService, times(1)).scheduleMaintenance(any(), any(), any(), any());
    }

    @Test
    void testDeleteFlight() throws Exception {
        when(flightOperationsService.deleteFlight(anyString())).thenReturn(true);

        mockMvc.perform(delete("/operations/flights/AA123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(flightOperationsService, times(1)).deleteFlight("AA123");
    }

    @Test
    void testGetFlightDetails() throws Exception {
        FlightModel flight = new FlightModel();
        flight.setFlightNumber("AA123");
        flight.setAirlineCode("AA");
        flight.setOrigin("JFK");
        flight.setDestination("LAX");

        Map<String, Object> flightDetails = new HashMap<>();
        flightDetails.put("flight", flight);

        when(flightOperationsService.getFlightDetails(anyString())).thenReturn(flightDetails);

        mockMvc.perform(get("/operations/flights/AA123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flight.flightNumber").value("AA123"))
                .andExpect(jsonPath("$.flight.airlineCode").value("AA"))
                .andExpect(jsonPath("$.flight.origin").value("JFK"))
                .andExpect(jsonPath("$.flight.destination").value("LAX"));

        verify(flightOperationsService, times(1)).getFlightDetails("AA123");
    }

    @Test
    void testUpdateAircraftStatus() throws Exception {
        when(flightOperationsService.updateAircraftStatus(anyString(), any(AircraftModel.AircraftStatus.class), anyString())).thenReturn(true);

        mockMvc.perform(post("/operations/aircraft/update")
                .param("registrationNumber", "N12345")
                .param("status", "MAINTENANCE")
                .param("location", "JFK"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(flightOperationsService, times(1)).updateAircraftStatus("N12345", AircraftModel.AircraftStatus.MAINTENANCE, "JFK");
    }
}