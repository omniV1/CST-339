package com.gcu.controller;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import com.gcu.model.OrderModel;
import com.gcu.model.UserModel;

@Controller
@RequestMapping("/app")
public class TestController {
    
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("title", "Microservices Demo");
        return "home";
    }
    
    @GetMapping("/getusers")
    public String getUsers(Model model) {
        // Call the REST API running at localhost on port 8081
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<List<UserModel>> responseEntity = restTemplate.exchange(
                "http://localhost:8081/service/users",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<UserModel>>() {}
        );
        List<UserModel> users = responseEntity.getBody();
        
        model.addAttribute("title", "Users");
        model.addAttribute("users", users);
        return "users";
    }
    
    @GetMapping("/getorders")
    public String getOrders(Model model) {
        // Call the REST API running at localhost on port 8082
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<List<OrderModel>> responseEntity = restTemplate.exchange(
                "http://localhost:8082/service/orders",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<OrderModel>>() {}
        );
        List<OrderModel> orders = responseEntity.getBody();
        
        model.addAttribute("title", "Orders");
        model.addAttribute("orders", orders);
        return "orders";
    }
} 