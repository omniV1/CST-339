package com.gcu.agms.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.gcu.agms.model.auth.UserModel;
import com.gcu.agms.model.auth.UserRole;
import com.gcu.agms.repository.UserRepository;

class JdbcUserServiceTest {

    @Mock
    private UserRepository userRepository;
    
    @InjectMocks
    private JdbcUserService userService;
    
    private UserModel testUser;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Create a test user
        testUser = new UserModel();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("Test123!");
        testUser.setEmail("test@example.com");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setPhoneNumber("+1234567890");
        testUser.setRole(UserRole.PUBLIC);
        testUser.setActive(true);
        testUser.setEnabled(true);
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());
    }
    
    @Test
    void testFindByUsername() {
        // Set up the mock repository
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        
        // Call the service method
        Optional<UserModel> result = userService.findByUsername("testuser");
        
        // Verify the result and repository interaction
        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
        verify(userRepository, times(1)).findByUsername("testuser");
    }
    
    @Test
    void testRegisterUserSuccess() {
        // Set up the mock repository
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.save(any(UserModel.class))).thenReturn(testUser);
        
        // Create a new user for registration
        UserModel newUser = new UserModel();
        newUser.setUsername("newuser");
        newUser.setPassword("Test123!");
        newUser.setEmail("new@example.com");
        newUser.setFirstName("New");
        newUser.setLastName("User");
        newUser.setPhoneNumber("+1234567890");
        newUser.setRole(UserRole.PUBLIC);
        
        // Call the service method
        boolean result = userService.registerUser(newUser);
        
        // Verify the result and repository interaction
        assertTrue(result);
        verify(userRepository, times(1)).existsByUsername("newuser");
        verify(userRepository, times(1)).save(any(UserModel.class));
    }
    
    @Test
    void testRegisterUserWithExistingUsername() {
        // Set up the mock repository
        when(userRepository.existsByUsername("existinguser")).thenReturn(true);
        
        // Create a new user for registration
        UserModel newUser = new UserModel();
        newUser.setUsername("existinguser");
        newUser.setPassword("Test123!");
        newUser.setEmail("new@example.com");
        newUser.setFirstName("New");
        newUser.setLastName("User");
        newUser.setPhoneNumber("+1234567890");
        newUser.setRole(UserRole.PUBLIC);
        
        // Call the service method
        boolean result = userService.registerUser(newUser);
        
        // Verify the result and repository interaction
        assertFalse(result);
        verify(userRepository, times(1)).existsByUsername("existinguser");
        verify(userRepository, never()).save(any(UserModel.class));
    }
    
    @Test
    void testGetAllUsers() {
        // Set up the mock repository
        when(userRepository.findAll()).thenReturn(List.of(testUser));
        
        // Call the service method
        List<UserModel> users = userService.getAllUsers();
        
        // Verify the result and repository interaction
        assertEquals(1, users.size());
        assertEquals("testuser", users.get(0).getUsername());
        verify(userRepository, times(1)).findAll();
    }
    
    @Test
    void testDeleteUser() {
        // Set up the mock repository
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        
        // Call the service method
        boolean result = userService.deleteUser(1L);
        
        // Verify the result and repository interaction
        assertTrue(result);
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }
    
    @Test
    void testUpdateUser() {
        // Set up the mock repository
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(UserModel.class))).thenReturn(testUser);
        
        // Create updated user with all required fields
        UserModel updatedUser = new UserModel();
        updatedUser.setId(1L);
        updatedUser.setUsername("testuser");
        updatedUser.setEmail("updated@example.com");
        updatedUser.setFirstName("Updated");
        updatedUser.setLastName("User");
        updatedUser.setPhoneNumber("+1234567890");
        updatedUser.setRole(UserRole.PUBLIC);
        updatedUser.setActive(true);
        updatedUser.setEnabled(true);
        
        // Call the service method
        boolean result = userService.updateUser(updatedUser);
        
        // Verify the result and repository interaction
        assertTrue(result);
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(any(UserModel.class));
    }
}