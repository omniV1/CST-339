package com.gcu.topic03;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.SessionScope;

import com.gcu.topic03.business.OrdersBusinessService;
import com.gcu.topic03.business.OrdersBusinessServiceInterface;

@Configuration
public class SpringConfig {

    @Bean(name="ordersBusinessService", initMethod="init", destroyMethod="destroy")
    @SessionScope
    public OrdersBusinessServiceInterface getOrdersBusinessService() {
        return new OrdersBusinessService();
    }
}
