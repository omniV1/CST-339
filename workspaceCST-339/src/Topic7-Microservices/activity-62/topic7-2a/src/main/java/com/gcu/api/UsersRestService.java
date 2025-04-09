package com.gcu.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gcu.business.UserBusinessService;
import com.gcu.model.UserModel;

@RestController
@RequestMapping("/service")
public class UsersRestService {
    
    private UserBusinessService service;
    
    @Autowired
    public UsersRestService(UserBusinessService service) {
        this.service = service;
    }
    
    @GetMapping("/users")
    public ResponseEntity<?> getUsers() {
        try {
            List<UserModel> users = service.getAllUsers();
            return new ResponseEntity<>(users, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
} 