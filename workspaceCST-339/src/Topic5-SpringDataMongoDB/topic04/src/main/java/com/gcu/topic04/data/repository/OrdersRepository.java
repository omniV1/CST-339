package com.gcu.topic04.data.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.gcu.topic04.data.entity.OrderEntity;

@Repository
public interface OrdersRepository extends MongoRepository<OrderEntity, String> {
    // All basic CRUD methods are automatically provided by MongoRepository
    // You can add custom query methods here if needed
}