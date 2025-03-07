package com.gcu.topic04.data.repository;

import java.util.List;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;

import com.gcu.topic04.data.entity.OrderEntity;

public interface OrdersRepository extends CrudRepository<OrderEntity, Long> {
    
    @Override
    @Query("SELECT * FROM ORDERS")
    @NonNull
    List<OrderEntity> findAll();
}
