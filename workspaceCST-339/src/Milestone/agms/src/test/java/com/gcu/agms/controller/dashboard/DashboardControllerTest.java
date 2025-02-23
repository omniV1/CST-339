package com.gcu.agms.controller.dashboard;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@DisplayName("Dashboard Controller Tests")
class DashboardControllerTest {

    private MockMvc mockMvc;
    private DashboardController controller;
    private MockHttpSession session;

    @BeforeEach
    public void setUp() {
        controller = new DashboardController();
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        session = new MockHttpSession();
    }

    @Test
    @DisplayName("Should redirect admin to admin dashboard")
    void testRedirectAdminToDashboard() throws Exception {
        session.setAttribute("userRole", "ADMIN");
        mockMvc.perform(get("/dashboard").session(session))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/admin/dashboard"));
    }

    @Test
    @DisplayName("Should redirect operations manager to operations dashboard")
    void testRedirectOperationsToDashboard() throws Exception {
        session.setAttribute("userRole", "OPERATIONS_MANAGER");
        mockMvc.perform(get("/dashboard").session(session))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/operations/dashboard"));
    }

    @Test
    @DisplayName("Should redirect gate manager to gates dashboard") 
    void testRedirectGateToDashboard() throws Exception {
        session.setAttribute("userRole", "GATE_MANAGER");
        mockMvc.perform(get("/dashboard").session(session))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/gates/dashboard"));
    }

    @Test
    @DisplayName("Should redirect airline staff to airline dashboard")
    void testRedirectAirlineToDashboard() throws Exception {
        session.setAttribute("userRole", "AIRLINE_STAFF");
        mockMvc.perform(get("/dashboard").session(session))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/airline/dashboard"));
    }

    @Test
    @DisplayName("Should redirect to home for unknown role")
    void testRedirectUnknownRole() throws Exception {
        session.setAttribute("userRole", "UNKNOWN");
        mockMvc.perform(get("/dashboard").session(session))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/"));
    }
}