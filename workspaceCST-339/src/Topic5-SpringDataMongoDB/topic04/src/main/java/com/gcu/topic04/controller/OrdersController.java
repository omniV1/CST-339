package com.gcu.topic04.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.gcu.topic04.business.OrdersBusinessServiceInterface;

@Controller
@RequestMapping("/orders")
public class OrdersController {
    
    @Autowired
    private OrdersBusinessServiceInterface ordersService;
    
    @GetMapping("")
    public String displayOrders(Model model) {
        // Add orders to the model
        model.addAttribute("orders", ordersService.getOrders());
        model.addAttribute("title", "Orders List");
        
        // Return the view name
        return "orders";
    }
}