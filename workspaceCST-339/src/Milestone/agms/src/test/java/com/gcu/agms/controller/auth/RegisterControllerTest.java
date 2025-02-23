package com.gcu.agms.controller.auth;

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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import com.gcu.agms.model.auth.UserModel;
import com.gcu.agms.model.auth.UserRole;
import com.gcu.agms.service.auth.AuthorizationCodeService;
import com.gcu.agms.service.auth.UserService;

@DisplayName("Register Controller Tests")
class RegisterControllerTest {

    private MockMvc mockMvc;
    private UserService userService;
    private AuthorizationCodeService authCodeService;
    private RegisterController controller;
  
   

    @BeforeEach
    protected void setUp() {
        userService = mock(UserService.class);
        authCodeService = mock(AuthorizationCodeService.class);
        controller = new RegisterController(userService, authCodeService);
        
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/views/");
        viewResolver.setSuffix(".html");
        
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                                .setViewResolvers(viewResolver)
                                .build();
                                
       
    }

    @Test
    @DisplayName("Should show registration page")
    void testShowRegisterPage() throws Exception {
        mockMvc.perform(get("/auth/register"))
               .andExpect(status().isOk())
               .andExpect(view().name("register"))
               .andExpect(model().attributeExists("userModel"))
               .andExpect(model().attribute("pageTitle", "Register - AGMS"));
    }

    @Test
    @DisplayName("Should register new user successfully")
    void testRegisterSuccess() throws Exception {
        when(userService.registerUser(any(UserModel.class))).thenReturn(true);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("userRole", "PUBLIC");

        mockMvc.perform(post("/auth/register")
                .session(session)  // Add session to request
                .param("username", "newuser")
                .param("password", "Password123!")
                .param("firstName", "Test")  // Add required fields
                .param("lastName", "User")
                .param("email", "test@test.com")
                .param("phoneNumber", "1234567890")
                .param("role", "PUBLIC"))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/auth/login"))
               .andExpect(flash().attributeExists("successMessage"));
    }

    @Test
    @DisplayName("Should handle registration validation errors")
    void testRegisterValidationError() throws Exception {
        mockMvc.perform(post("/auth/register")  // Update URL
                .param("username", "")
                .param("password", ""))
               .andExpect(status().isOk())
               .andExpect(view().name("register"));
    }

    @Test
    @DisplayName("Should validate admin authorization code")
    void testRegisterAdminWithAuthCode() throws Exception {
        when(authCodeService.isValidAuthCode("VALID_CODE", UserRole.ADMIN)).thenReturn(true);
        when(userService.registerUser(any(UserModel.class))).thenReturn(true);

        mockMvc.perform(post("/auth/register")
                .param("username", "admin")
                .param("password", "Admin123!")
                .param("firstName", "Admin")  // Add required fields
                .param("lastName", "User")
                .param("email", "admin@test.com")
                .param("phoneNumber", "1234567890")
                .param("role", "ADMIN")
                .param("authCode", "VALID_CODE"))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/auth/login"));
    }
}

