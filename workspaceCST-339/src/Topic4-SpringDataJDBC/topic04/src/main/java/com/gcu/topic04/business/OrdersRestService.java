package com.gcu.topic04.business;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gcu.topic04.model.OrderList;
import com.gcu.topic04.model.OrderModel;

@RestController
@RequestMapping("/service")
public class OrdersRestService {

	@Autowired
	private OrdersBusinessServiceInterface ordersBusinessServiceInterface;

	@GetMapping(path = "/getjson", produces = { MediaType.APPLICATION_JSON_VALUE })
	public List<OrderModel> getOrdersAsJson() {
		return ordersBusinessServiceInterface.getOrders();
	}

	@GetMapping(path = "/getxml", produces = { MediaType.APPLICATION_XML_VALUE })
	public OrderList getOrdersAsXml() {
		OrderList orderList = new OrderList();
		orderList.setOrders(ordersBusinessServiceInterface.getOrders());
		return orderList;
	}
}
