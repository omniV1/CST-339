package com.gcu.topic04.business;

import java.util.List;

import com.gcu.topic04.model.OrderModel;

public interface OrdersBusinessServiceInterface {
    public void test();
    public List<OrderModel> getOrders();
    
    // Add lifecycle methods
    public void init();
    public void destroy();
}
