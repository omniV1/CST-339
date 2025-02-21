package com.gcu.agms.service.impl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import com.gcu.agms.model.auth.UserModel;
import com.gcu.agms.model.auth.UserRole;

public class InMemoryUserServiceTest {

    @InjectMocks
    private InMemoryUserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService.initializeTestUsers();
    }

    @Test
    void testRegisterUser() {
        UserModel newUser = new UserModel();
        newUser.setUsername("newuser");
        newUser.setPassword("NewUser123!");
        newUser.setEmail("newuser@agms.com");
        newUser.setRole(UserRole.PUBLIC);

        boolean result = userService.registerUser(newUser);
        assertTrue(result);

        Optional<UserModel> foundUser = userService.findByUsername("newuser");
        assertTrue(foundUser.isPresent());
        assertEquals("newuser", foundUser.get().getUsername());
    }

    @Test
    void testFindByUsername() {
        Optional<UserModel> user = userService.findByUsername("admin");
        assertTrue(user.isPresent());
        assertEquals("admin", user.get().getUsername());
    }
}