package com.gcu.topic03.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gcu.topic03.business.OrdersBusinessServiceInterface;
import com.gcu.topic03.model.OrderList;

@RestController
@RequestMapping("/service")  // Changed base path to match the error
public class OrdersRestService {

    @Autowired
    private OrdersBusinessServiceInterface ordersBusinessService;

    @GetMapping(value = "/getjson", produces = MediaType.APPLICATION_JSON_VALUE)
    public OrderList getOrdersAsJson() {
        OrderList orderList = new OrderList();
        orderList.setOrders(ordersBusinessService.getOrders());
        return orderList;
    }

    @GetMapping(value = "/getxml", produces = MediaType.APPLICATION_XML_VALUE)
    public OrderList getOrdersAsXml() {
        OrderList orderList = new OrderList();
        orderList.setOrders(ordersBusinessService.getOrders());
        return orderList;
    }
}
