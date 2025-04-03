package cst339;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cst339.business.OrdersBusinessService;
import cst339.business.OrdersBusinessServiceInterface;

@Configuration
public class SpringConfig {

	// Call orderBusinessService init method when starting and call destroy method
	// when finished with the Bean
	@Bean(name = "ordersBusinessServce", initMethod = "init", destroyMethod = "destroy")
//	@RequestScope
//	@SessionScope
//	@Scope(value="prototype", proxyMode=ScopedProxyMode.TARGET_CLASS)
	public OrdersBusinessServiceInterface getOrdersBusiness() {

		System.out.println(">>>>> SpringConfig.getOrdersBusiness");
		return new OrdersBusinessService();
	}

}
