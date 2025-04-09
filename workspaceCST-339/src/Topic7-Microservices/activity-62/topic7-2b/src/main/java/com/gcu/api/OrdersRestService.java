package com.gcu.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gcu.business.OrdersBusinessService;
import com.gcu.model.OrderModel;

@RestController
@RequestMapping("/service")
public class OrdersRestService {
    
    private OrdersBusinessService service;
    
    @Autowired
    public OrdersRestService(OrdersBusinessService service) {
        this.service = service;
    }
    
    @GetMapping("/orders")
    public ResponseEntity<?> getOrders() {
        try {
            List<OrderModel> orders = service.getAllOrders();
            return new ResponseEntity<>(orders, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
} 