package com.gcu.data.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.gcu.data.entity.OrderEntity;

public interface OrdersRepository extends MongoRepository<OrderEntity, String> {
    
    // We don't need to override findById as it's already provided by MongoRepository
    // The standard method signature returns Optional<OrderEntity>
} 