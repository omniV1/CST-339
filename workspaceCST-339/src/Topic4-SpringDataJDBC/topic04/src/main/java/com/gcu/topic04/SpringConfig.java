package com.gcu.topic04;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.SessionScope;

import com.gcu.topic04.business.OrdersBusinessService;
import com.gcu.topic04.business.OrdersBusinessServiceInterface;

@Configuration
public class SpringConfig {

	@Bean(name="ordersBusinessServce", initMethod="init", destroyMethod="destroy")
	@SessionScope
	public OrdersBusinessServiceInterface getOrdersBusiness() {
		
		return new OrdersBusinessService();
	}
}
