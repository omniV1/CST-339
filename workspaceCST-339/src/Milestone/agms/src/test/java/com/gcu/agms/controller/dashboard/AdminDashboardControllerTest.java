package com.gcu.agms.controller.dashboard;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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

    @Test
    @DisplayName("Should redirect unauthorized access to login")
    void testUnauthorizedAccess() throws Exception {
        session.setAttribute("userRole", "PUBLIC");
        mockMvc.perform(get("/admin/dashboard").session(session))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/login"));
    }

    @Test
    @DisplayName("Should show create gate form")
    void testShowCreateGateForm() throws Exception {
        mockMvc.perform(get("/admin/gates/create").session(session))
               .andExpect(status().isOk())
               .andExpect(view().name("admin/gate-form"))
               .andExpect(model().attributeExists("gateModel"))
               .andExpect(model().attribute("pageTitle", "Create New Gate - AGMS"));
    }

    @Test
    @DisplayName("Should create new gate successfully")
    void testCreateGate() throws Exception {
        // Create a more complete test gate
        GateModel gate = new GateModel();
        gate.setGateId("T1G1");
        gate.setTerminal("1");
        gate.setGateNumber("1");
        gate.setGateType(GateModel.GateType.DOMESTIC);
        gate.setGateSize(GateModel.GateSize.MEDIUM);
        gate.setIsActive(true);
        gate.setHasJetBridge(true);
        gate.setCapacity(100);
        
        when(gateManagementService.createGate(any(GateModel.class))).thenReturn(true);

        mockMvc.perform(post("/admin/gates/create")
               .session(session)
               .flashAttr("gateModel", gate))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/admin/gates"))
               .andExpect(flash().attribute("success", "Gate " + gate.getGateId() + " created successfully"));

        verify(gateManagementService).createGate(any(GateModel.class));
    }


    @Test
    @DisplayName("Should show add user form")
    void testShowAddUserForm() throws Exception {
        mockMvc.perform(get("/admin/users/add").session(session))
               .andExpect(status().isOk())
               .andExpect(view().name("admin/user-form"))
               .andExpect(model().attributeExists("userModel"))
               .andExpect(model().attribute("pageTitle", "Add New User - AGMS"));
    }

    @Test
    @DisplayName("Should add new user successfully")
    void testAddUser() throws Exception {
        // Create a complete test user with all required fields
        UserModel user = new UserModel();
        user.setUsername("newuser");
        user.setPassword("Password123!"); // Valid password
        user.setEmail("newuser@test.com");
        user.setFirstName("New");
        user.setLastName("User");
        user.setPhoneNumber("+1234567890");
        user.setRole(UserRole.PUBLIC);
        
        when(userService.registerUser(any(UserModel.class))).thenReturn(true);

        mockMvc.perform(post("/admin/users/add")
               .session(session)
               .flashAttr("userModel", user))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/admin/dashboard"))
               .andExpect(flash().attribute("success", "User " + user.getUsername() + " created successfully"));

        verify(userService).registerUser(any(UserModel.class));
    }

    @Test
    @DisplayName("Should delete user successfully")
    void testDeleteUser() throws Exception {
        when(userService.deleteUser(1L)).thenReturn(true);

        mockMvc.perform(post("/admin/users/delete/1").session(session))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/admin/dashboard"))
               .andExpect(flash().attributeExists("success"));

        verify(userService).deleteUser(1L);
    }

    @Test
    @DisplayName("Should show edit user form")
    void testShowEditUserForm() throws Exception {
        UserModel user = createTestUser("existinguser", UserRole.PUBLIC);
        user.setId(1L);
        when(userService.getUserById(1L)).thenReturn(user);

        mockMvc.perform(get("/admin/users/edit/1").session(session))
               .andExpect(status().isOk())
               .andExpect(view().name("admin/user-form"))
               .andExpect(model().attributeExists("userModel"))
               .andExpect(model().attribute("pageTitle", "Edit User - AGMS"));

        verify(userService).getUserById(1L);
    }

    @Test
    @DisplayName("Should update user successfully")
    void testUpdateUser() throws Exception {
        UserModel user = new UserModel();
        user.setId(1L);
        user.setUsername("updateduser");
        user.setPassword("Password123!"); // Add valid password
        user.setEmail("user@test.com");   // Add valid email
        user.setFirstName("Updated");     // Add required fields
        user.setLastName("User");
        user.setRole(UserRole.PUBLIC);
        user.setPhoneNumber("+1234567890");

        when(userService.updateUser(any(UserModel.class))).thenReturn(true);

        mockMvc.perform(post("/admin/users/update/1")
               .session(session)
               .flashAttr("userModel", user))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/admin/dashboard"))
               .andExpect(flash().attribute("success", "User " + user.getUsername() + " updated successfully"));

        verify(userService).updateUser(any(UserModel.class));
    }

    @Test
    @DisplayName("Should handle user update failure")
    void testUpdateUserFailure() throws Exception {
        UserModel user = new UserModel();
        user.setId(1L);
        user.setUsername("failuser");
        user.setPassword("Password123!");
        user.setEmail("fail@test.com");
        user.setFirstName("Fail");
        user.setLastName("User");
        user.setRole(UserRole.PUBLIC);
        user.setPhoneNumber("+1234567890");

        when(userService.updateUser(any(UserModel.class))).thenThrow(new RuntimeException("Update failed"));

        mockMvc.perform(post("/admin/users/update/1")
               .session(session)
               .flashAttr("userModel", user))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/admin/dashboard"))
               .andExpect(flash().attribute("error", "Failed to update user: Update failed"));
    }

    private UserModel createTestUser(String username, UserRole role) {
        UserModel user = new UserModel();
        user.setUsername(username);
        user.setPassword("Password123!"); // Set valid default password
        user.setEmail(username + "@test.com"); // Set valid default email
        user.setFirstName("Test");
        user.setLastName("User");
        user.setPhoneNumber("+1234567890");
        user.setRole(role);
        return user;
    }

    private GateModel createTestGate(String gateId) {
        GateModel gate = new GateModel();
        gate.setGateId(gateId);
        gate.setTerminal(gateId.substring(1,2));
        gate.setGateNumber(gateId.substring(3));
        gate.setGateType(GateModel.GateType.DOMESTIC);
        gate.setGateSize(GateModel.GateSize.MEDIUM);
        gate.setIsActive(true);
        gate.setHasJetBridge(true);
        gate.setCapacity(100);
        return gate;
    }
}