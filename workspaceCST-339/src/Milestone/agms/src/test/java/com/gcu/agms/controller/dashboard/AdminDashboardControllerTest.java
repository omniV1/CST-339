package com.gcu.agms.controller.dashboard;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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

import com.gcu.agms.model.auth.UserModel;
import com.gcu.agms.model.auth.UserRole;
import com.gcu.agms.model.gate.GateModel;
import com.gcu.agms.service.auth.UserService;
import com.gcu.agms.service.gate.GateManagementService;
import com.gcu.agms.service.gate.GateOperationsService;

@DisplayName("Admin Dashboard Controller Tests")
class AdminDashboardControllerTest {

    private MockMvc mockMvc;
    private UserService userService;
    private GateOperationsService gateOperationsService;
    private GateManagementService gateManagementService;
    private AdminDashboardController controller;
    private MockHttpSession session;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        gateOperationsService = mock(GateOperationsService.class);
        gateManagementService = mock(GateManagementService.class);
        
        controller = new AdminDashboardController(
            userService, 
            gateOperationsService, 
            gateManagementService
        );
        
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        session = new MockHttpSession();
        session.setAttribute("userRole", "ADMIN");
    }

    @Test
    @DisplayName("Should show admin dashboard")
    void testShowDashboard() throws Exception {
        // Mock service responses
        when(userService.getAllUsers()).thenReturn(Arrays.asList(
            createTestUser("admin1", UserRole.ADMIN),
            createTestUser("user1", UserRole.PUBLIC)
        ));

        Map<String, Integer> stats = new HashMap<>();
        stats.put("totalGates", 10);
        stats.put("availableGates", 5);
        when(gateOperationsService.getStatistics()).thenReturn(stats);

        // Perform request
        mockMvc.perform(get("/admin/dashboard").session(session))
               .andExpect(status().isOk())
               .andExpect(view().name("dashboard/admin"))
               .andExpect(model().attributeExists("users", "totalUsers", "gateStats"))
               .andExpect(model().attribute("pageTitle", "Admin Dashboard - AGMS"));
    }

    @Test
    @DisplayName("Should show user management page")
    void testShowUsers() throws Exception {
        when(userService.getAllUsers()).thenReturn(Arrays.asList(
            createTestUser("admin1", UserRole.ADMIN),
            createTestUser("user1", UserRole.PUBLIC)
        ));

        mockMvc.perform(get("/admin/users").session(session))
               .andExpect(status().isOk())
               .andExpect(view().name("admin/users"))
               .andExpect(model().attributeExists("users"))
               .andExpect(model().attribute("pageTitle", "User Management - AGMS"));
    }

    @Test
    @DisplayName("Should show gate management page")
    void testShowGateManagement() throws Exception {
        when(gateManagementService.getAllGates()).thenReturn(Arrays.asList(
            createTestGate("T1G1"),
            createTestGate("T1G2")
        ));

        mockMvc.perform(get("/admin/gates").session(session))
               .andExpect(status().isOk())
               .andExpect(view().name("admin/gates"))
               .andExpect(model().attributeExists("gates"))
               .andExpect(model().attribute("pageTitle", "Gate Management - AGMS"));
    }

    @Test
    @DisplayName("Should show system health page")
    void testShowSystemHealth() throws Exception {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("totalGates", 10);
        stats.put("availableGates", 5);
        when(gateOperationsService.getStatistics()).thenReturn(stats);

        mockMvc.perform(get("/admin/system-health").session(session))
               .andExpect(status().isOk())
               .andExpect(view().name("admin/system-health"))
               .andExpect(model().attributeExists("gateStats"))
               .andExpect(model().attribute("pageTitle", "System Health - AGMS"));
    }

    private UserModel createTestUser(String username, UserRole role) {
        UserModel user = new UserModel();
        user.setUsername(username);
        user.setRole(role);
        return user;
    }

    private GateModel createTestGate(String gateId) {
        GateModel gate = new GateModel();
        gate.setGateId(gateId);
        return gate;
    }
}