package com.gcu.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
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
    
    @Value("${services.user-service.url:http://localhost:8081}")
    private String userServiceUrl;
    
    @Value("${services.order-service.url:http://localhost:8082}")
    private String orderServiceUrl;
    
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("title", "Microservices Demo");
        return "home";
    }
    
    @GetMapping("/getusers")
    public String getUsers(Model model) {
        try {
            // Call the REST API using the configured URL
            String url = userServiceUrl + "/service/users";
            
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<List<UserModel>> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<UserModel>>() {}
            );
            List<UserModel> users = responseEntity.getBody();
            
            model.addAttribute("title", "Users");
            model.addAttribute("users", users);
            return "users";
        } catch (Exception e) {
            model.addAttribute("title", "Error");
            model.addAttribute("message", "Error retrieving users: " + e.getMessage());
            return "error";
        }
    }
    
    @GetMapping("/getorders")
    public String getOrders(Model model) {
        try {
            // Call the REST API using the configured URL
            String url = orderServiceUrl + "/service/orders";
            
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<List<OrderModel>> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<OrderModel>>() {}
            );
            List<OrderModel> orders = responseEntity.getBody();
            
            model.addAttribute("title", "Orders");
            model.addAttribute("orders", orders);
            return "orders";
        } catch (Exception e) {
            model.addAttribute("title", "Error");
            model.addAttribute("message", "Error retrieving orders: " + e.getMessage());
            return "error";
        }
    }
} 