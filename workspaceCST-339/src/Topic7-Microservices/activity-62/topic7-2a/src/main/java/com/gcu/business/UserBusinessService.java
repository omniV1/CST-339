package com.gcu.business;

import java.util.ArrayList;
import java.util.List;


import org.springframework.stereotype.Service;

import com.gcu.data.entity.UserEntity;
import com.gcu.data.repository.UsersRepository;
import com.gcu.model.UserModel;

@Service
public class UserBusinessService {
    
    private final UsersRepository usersRepository;
    
   
    public UserBusinessService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }
    
    public List<UserModel> getAllUsers() {
        // Get all users from the database
        List<UserEntity> usersEntity = usersRepository.findAll();
        
        // Convert UserEntity list to UserModel list
        List<UserModel> usersDomain = new ArrayList<UserModel>();
        for (UserEntity entity : usersEntity) {
            usersDomain.add(new UserModel(entity.getId(), entity.getUsername(), entity.getPassword()));
        }
        
        return usersDomain;
    }
} 