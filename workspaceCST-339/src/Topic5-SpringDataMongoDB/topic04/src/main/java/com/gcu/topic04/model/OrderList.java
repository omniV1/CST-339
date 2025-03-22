package com.gcu.topic04.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="orders")
public class OrderList {
    private List<OrderModel> orders = new ArrayList<>();
    
    @XmlElement(name="order")
    public List<OrderModel> getOrders() {
        return orders;
    }
    
    public void setOrders(List<OrderModel> orders) {
        this.orders = orders;
    }
}