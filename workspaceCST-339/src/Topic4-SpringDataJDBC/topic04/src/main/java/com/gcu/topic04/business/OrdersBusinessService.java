package com.gcu.topic04.business;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.gcu.topic04.data.OrdersDataService;
import com.gcu.topic04.data.entity.OrderEntity;
import com.gcu.topic04.model.OrderModel;

public class OrdersBusinessService implements OrdersBusinessServiceInterface {
    
    @Autowired
    private OrdersDataService ordersDataService;
    
    @Override
    public void test() {
        System.out.println("OrdersBusinessService is working properly");
    }
    
    @Override
    public List<OrderModel> getOrders() {
        // Get order entities from data service
        List<OrderEntity> orderEntities = ordersDataService.findAll();
        
        // Convert entities to models
        List<OrderModel> orderModels = new ArrayList<>();
        for (OrderEntity entity : orderEntities) {
            orderModels.add(new OrderModel(
                entity.getId(),
                entity.getOrderNo(),
                entity.getProductName(),
                entity.getPrice(),
                entity.getQuantity()
            ));
        }
        
        return orderModels;
    }
    
    @Override
    public void init() {
        System.out.println("OrdersBusinessService initialized");
    }
    
    @Override
    public void destroy() {
        System.out.println("OrdersBusinessService destroyed");
    }
}
