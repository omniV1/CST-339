package com.gcu.business;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.gcu.data.entity.OrderEntity;
import com.gcu.data.repository.OrdersRepository;
import com.gcu.model.OrderModel;

@Service
public class OrdersBusinessService {
    
    private final OrdersRepository ordersRepository;
    
   
    public OrdersBusinessService(OrdersRepository ordersRepository) {
        this.ordersRepository = ordersRepository;
    }
    
    public List<OrderModel> getAllOrders() {
        // Get all orders from the database
        List<OrderEntity> ordersEntity = ordersRepository.findAll();
        
        // Convert OrderEntity list to OrderModel list
        List<OrderModel> ordersDomain = new ArrayList<>();
        for (OrderEntity entity : ordersEntity) {
            ordersDomain.add(new OrderModel(entity.getId(), entity.getOrderNo(), entity.getProductName(),
                    entity.getPrice(), entity.getQuantity()));
        }
        
        return ordersDomain;
    }
} 