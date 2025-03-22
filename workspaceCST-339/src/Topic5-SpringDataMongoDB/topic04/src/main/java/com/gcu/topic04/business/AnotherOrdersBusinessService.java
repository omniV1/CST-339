package com.gcu.topic04.business;
import java.util.List;

import com.gcu.topic04.model.OrderModel;

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
		return null;
	}

	@Override
	public OrderModel getOrderById(String id) {
		// TODO Auto-generated method stub
		return null;
	}

}

