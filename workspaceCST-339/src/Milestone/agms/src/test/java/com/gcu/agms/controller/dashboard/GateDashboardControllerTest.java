package com.gcu.agms.controller.dashboard;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import com.gcu.agms.model.gate.AssignmentModel;
import com.gcu.agms.model.gate.GateModel;
import com.gcu.agms.service.flight.AssignmentService;
import com.gcu.agms.service.gate.GateManagementService;
import com.gcu.agms.service.gate.GateOperationsService;

@DisplayName("Gate Dashboard Controller Tests") 
class GateDashboardControllerTest {

    private MockMvc mockMvc;
    private GateOperationsService gateOperationsService;
    private GateManagementService gateManagementService;
    private AssignmentService assignmentService;
    private GateDashboardController controller;
    private MockHttpSession session;

    @BeforeEach
    void setUp() {
        gateOperationsService = mock(GateOperationsService.class);
        gateManagementService = mock(GateManagementService.class);
        assignmentService = mock(AssignmentService.class);
        
        controller = new GateDashboardController(
            gateOperationsService,
            gateManagementService, 
            assignmentService
        );

        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/views/");
        viewResolver.setSuffix(".html");

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                                .setViewResolvers(viewResolver)
                                .build();

        session = new MockHttpSession();
        session.setAttribute("userRole", "GATE_MANAGER");

        // Setup default mock behaviors
        when(gateManagementService.getAllGates()).thenReturn(Arrays.asList(new GateModel()));
        when(gateOperationsService.getAllGateStatuses()).thenReturn(new HashMap<>());
    }

    @Test
    @DisplayName("Should show gate dashboard")
    void testShowDashboard() throws Exception {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("totalGates", 10);
        stats.put("availableGates", 5);
        
        when(gateOperationsService.getStatistics()).thenReturn(stats);

        mockMvc.perform(get("/gates/dashboard").session(session))
               .andExpect(status().isOk())
               .andExpect(view().name("dashboard/gate"))
               .andExpect(model().attributeExists("gates"))
               .andExpect(model().attributeExists("gateStatuses"))
               .andExpect(model().attributeExists("statistics"))
               .andExpect(model().attribute("pageTitle", "Gate Management Dashboard - AGMS"));
    }

    @Test
    @DisplayName("Should show gate schedule") 
    void testShowSchedule() throws Exception {
        // Test setup
        String gateId = "T1G1";
        GateModel gate = new GateModel();
        gate.setGateId(gateId);
        
        // Mock dependencies
        when(gateManagementService.getGateById(gateId)).thenReturn(Optional.of(gate));
        when(assignmentService.getAssignmentsForGate(gateId)).thenReturn(Arrays.asList());
        when(gateOperationsService.getAllGateStatuses()).thenReturn(new HashMap<>());

        // Verify the endpoint returns the correct view and data
        mockMvc.perform(get("/gates/details/{gateId}/schedule", gateId).session(session)) // Change URL to match controller
               .andExpect(status().isOk())
               .andExpect(view().name("gates/schedule"))
               .andExpect(model().attributeExists("gate"))
               .andExpect(model().attributeExists("assignments")) // Change from status to assignments
               .andExpect(model().attribute("pageTitle", "Gate Schedule - AGMS"));
    }

    @Test
    @DisplayName("Should create assignment")
    void testCreateAssignment() throws Exception {
        when(assignmentService.createAssignment(any(AssignmentModel.class))).thenReturn(true);

        mockMvc.perform(post("/gates/assignments/create")
                .session(session)
                .param("gateId", "T1G1")
                .param("flightNumber", "AA123"))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/gates/dashboard"))
               .andExpect(flash().attributeExists("success"));
    }

    @Test
    @DisplayName("Should report issue")
    void testReportIssue() throws Exception {
        mockMvc.perform(post("/gates/report-issue/{gateId}", "T1G1")
                .session(session)
                .param("issueDescription", "Test issue"))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/gates/details/T1G1"))
               .andExpect(flash().attributeExists("success"));
    }

    @Test
    @DisplayName("Should show gate details")
    void testShowGateDetails() throws Exception {
        // Setup test gate
        String gateId = "T1G1"; 
        GateModel gate = new GateModel();
        gate.setGateId(gateId);
        
        // Mock services
        when(gateManagementService.getGateById(gateId)).thenReturn(Optional.of(gate));
        Map<String, GateOperationsService.GateStatus> statuses = new HashMap<>();
        statuses.put(gateId, GateOperationsService.GateStatus.AVAILABLE);
        when(gateOperationsService.getAllGateStatuses()).thenReturn(statuses);

        mockMvc.perform(get("/gates/details/{gateId}", gateId).session(session))
               .andExpect(status().isOk())
               .andExpect(view().name("gates/details"))
               .andExpect(model().attributeExists("gate"))
               .andExpect(model().attributeExists("currentStatus"));
    }
    
    @Test
    @DisplayName("Should handle gate details not found")
    void testShowGateDetailsNotFound() throws Exception {
        when(gateManagementService.getGateById("INVALID")).thenReturn(Optional.empty());

        mockMvc.perform(get("/gates/details/INVALID").session(session))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/gates/dashboard"));
    }

    @Test
    @DisplayName("Should update gate status")
    void testUpdateGateStatus() throws Exception {
        mockMvc.perform(post("/gates/status/{gateId}", "T1G1")
                .session(session)
                .param("newStatus", "MAINTENANCE"))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/gates/details/T1G1"))
               .andExpect(flash().attributeExists("success"));
    }

    @Test
    @DisplayName("Should show maintenance schedule")
    void testShowMaintenanceSchedule() throws Exception {
        String gateId = "T1G1";
        GateModel gate = new GateModel();
        gate.setGateId(gateId);
        
        when(gateManagementService.getGateById(gateId)).thenReturn(Optional.of(gate));

        mockMvc.perform(get("/gates/maintenance/{gateId}", gateId).session(session))
               .andExpect(status().isOk())
               .andExpect(view().name("gates/maintenance"))
               .andExpect(model().attributeExists("gate"));
    }

    @Test
    @DisplayName("Should handle maintenance schedule not found") 
    void testShowMaintenanceScheduleNotFound() throws Exception {
        when(gateManagementService.getGateById("INVALID")).thenReturn(Optional.empty());

        mockMvc.perform(get("/gates/maintenance/INVALID").session(session))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/gates/dashboard"));
    }

    @Test
    @DisplayName("Should handle unauthorized access")
    void testUnauthorizedAccess() throws Exception {
        session.setAttribute("userRole", "PUBLIC");

        mockMvc.perform(get("/gates/dashboard").session(session))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/login"));
    }

    @Test
    @DisplayName("Should print gate schedule")
    void testPrintSchedule() throws Exception {
        GateModel gate = new GateModel();
        gate.setGateId("T1G1");
        when(gateManagementService.getAllGates()).thenReturn(Arrays.asList(gate));
        when(assignmentService.getAssignmentsForGate("T1G1")).thenReturn(Arrays.asList());

        mockMvc.perform(get("/gates/assignments/print").session(session))
               .andExpect(status().isOk())
               .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, 
                   "attachment; filename=gate-schedule.txt"))
               .andExpect(content().contentType(MediaType.TEXT_PLAIN_VALUE));
    }
}
