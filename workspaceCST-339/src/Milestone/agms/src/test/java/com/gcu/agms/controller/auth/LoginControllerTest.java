package com.gcu.agms.controller.auth;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import com.gcu.agms.model.auth.LoginModel;
import com.gcu.agms.model.auth.UserModel;
import com.gcu.agms.model.auth.UserRole;
import com.gcu.agms.service.auth.LoginService;

@DisplayName("Login Controller Tests")
class LoginControllerTest {

    private MockMvc mockMvc;
    private LoginService loginService;
    private LoginController controller;
    private MockHttpSession session;

    @BeforeEach
    void setUp() {
        loginService = mock(LoginService.class);
        controller = new LoginController(loginService);
        
        // Configure view resolver
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/views/");
        viewResolver.setSuffix(".html");
        
        // Setup MockMvc with view resolver
        mockMvc = MockMvcBuilders
            .standaloneSetup(controller)
            .setViewResolvers(viewResolver)
            .build();
            
        session = new MockHttpSession();
        
        // Setup default behavior for credential validation
        when(loginService.validateCredentials(any(LoginModel.class))).thenReturn(true);
    }

    @Test
    @DisplayName("Should display login page")
    void testDisplayLogin() throws Exception {
        mockMvc.perform(get("/auth/login"))
               .andExpect(status().isOk())
               .andExpect(model().attributeExists("loginModel"))
               .andExpect(model().attribute("pageTitle", "Login - AGMS"));
    }

    @Test
    @DisplayName("Should process successful login")
    void testProcessLoginSuccess() throws Exception {
        UserModel user = new UserModel();
        user.setUsername("admin");
        user.setRole(UserRole.ADMIN);

        when(loginService.authenticate(any(LoginModel.class)))
            .thenReturn(Optional.of(user));

        mockMvc.perform(post("/auth/login")
                .param("username", "admin")
                .param("password", "password"))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/admin/dashboard"));
    }

    @Test
    @DisplayName("Should handle failed login")
    void testProcessLoginFailure() throws Exception {
        when(loginService.authenticate(any(LoginModel.class)))
            .thenReturn(Optional.empty());

        mockMvc.perform(post("/auth/login")
                .param("username", "invalid")
                .param("password", "wrong"))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/auth/login"))
               .andExpect(flash().attributeExists("error"));
    }

    @Test
    @DisplayName("Should handle validation errors")
    void testProcessLoginValidationError() throws Exception {
        mockMvc.perform(post("/auth/login")
                .param("username", "")
                .param("password", ""))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/auth/login"))
               .andExpect(flash().attributeExists("error"));
    }

    @Test
    @DisplayName("Should process logout")
    void testProcessLogout() throws Exception {
        mockMvc.perform(get("/auth/logout")
                .session(session))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/auth/login"))
               .andExpect(flash().attributeExists("successMessage"));
    }
}