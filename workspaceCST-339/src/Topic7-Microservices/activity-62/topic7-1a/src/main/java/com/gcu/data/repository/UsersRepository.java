package com.gcu.data.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.gcu.data.entity.UserEntity;

public interface UsersRepository extends MongoRepository<UserEntity, String> {
    
    UserEntity findByUsername(String username);
} 