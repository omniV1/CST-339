package com.gcu.topic04;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.context.annotation.RequestScope;

import com.gcu.topic04.business.OrdersBusinessService;
import com.gcu.topic04.business.OrdersBusinessServiceInterface;
import com.gcu.topic04.business.SecurityBusinessService;

@Configuration
public class SpringConfig {
    
    @Bean(name="ordersBusinessService")
    @Scope(value="prototype", proxyMode=ScopedProxyMode.TARGET_CLASS)
    public OrdersBusinessServiceInterface getOrdersBusiness() {
        return new OrdersBusinessService();
    }
    
    // Optionally add AnotherOrdersBusinessService if needed
    /*
    @Bean(name="anotherOrdersBusinessService")
    public OrdersBusinessServiceInterface getAnotherOrdersBusiness() {
        return new AnotherOrdersBusinessService();
    }
    */
    
    @Bean(name="securityBusinessService")
    @RequestScope
    public SecurityBusinessService getSecurityService() {
        return new SecurityBusinessService();
    }
}
