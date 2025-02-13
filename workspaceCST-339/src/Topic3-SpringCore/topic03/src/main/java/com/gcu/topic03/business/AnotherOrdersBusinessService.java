package com.gcu.topic03.business;

import java.util.ArrayList;
import java.util.List;

import com.gcu.topic03.model.OrderModel;

public class AnotherOrdersBusinessService implements OrdersBusinessServiceInterface {

	@Override
	public void init() {
		System.out.println("AnotherOrdersBusinessService.init()");

	}

	@Override
	public void destroy() {
		System.out.println("AnotherOrdersBusinessService.destroy()");

	}

	@Override
	public void test() {

		System.out.println("AnotherOrdersBusinessService.test()");

	}

	@Override
	public List<OrderModel> getOrders() {
		List<OrderModel> orders = new ArrayList<OrderModel>();
		orders.add(new OrderModel(0L, "0000000010", "Product 10", 10.00f, 10));
		orders.add(new OrderModel(1L, "0000000011", "Product 11", 11.00f, 11));
		orders.add(new OrderModel(2L, "0000000012", "Product 12", 12.00f, 12));
		orders.add(new OrderModel(3L, "0000000013", "Product 13", 13.00f, 13));
		orders.add(new OrderModel(4L, "0000000014", "Product 14", 14.00f, 14));
		return orders;
	}

}
