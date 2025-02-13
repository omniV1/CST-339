package com.gcu.topic03.business;

import java.util.List;

import com.gcu.topic03.model.OrderModel;

public interface OrdersBusinessServiceInterface {

	public void init();
	public void destroy();
	public void test();
	public List<OrderModel> getOrders();
}
