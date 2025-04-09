package com.gcu.data.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.gcu.data.entity.OrderEntity;

public interface OrdersRepository extends MongoRepository<OrderEntity, String> {
    // Remove the conflicting findById method
    // MongoRepository already has findById(String id) that returns Optional<OrderEntity>
}