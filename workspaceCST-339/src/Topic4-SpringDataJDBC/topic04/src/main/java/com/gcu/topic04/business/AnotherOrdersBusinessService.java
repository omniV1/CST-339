package com.gcu.topic04.business;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.gcu.topic04.data.OrdersDataService;
import com.gcu.topic04.data.entity.OrderEntity;
import com.gcu.topic04.model.OrderModel;

public class AnotherOrdersBusinessService implements OrdersBusinessServiceInterface {
    
    @Autowired
    private OrdersDataService service;
    
    @Override
    public void init() {
        System.out.println("AnotherOrdersBusinessService initialized");
    }
    
    @Override
    public void destroy() {
        System.out.println("AnotherOrdersBusinessService destroyed");
    }
    
    @Override
    public void test() {
        System.out.println("Hello from the AnotherOrdersBusinessService");
    }
    
    @Override
    public List<OrderModel> getOrders() {
        // Get orders from the service
        List<OrderEntity> ordersEntity = service.findAll();
        
        // Create new list to hold OrderModel objects
        List<OrderModel> ordersDomain = new ArrayList<>();
        
        // For each order entity, create a new OrderModel
        for(OrderEntity entity : ordersEntity) {
            ordersDomain.add(new OrderModel(entity.getId(), entity.getOrderNo(), entity.getProductName(), entity.getPrice(), entity.getQuantity()));
        }
        
        return ordersDomain;
    }
}
