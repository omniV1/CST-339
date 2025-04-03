package cst339.business;

import java.util.List;

import cst339.model.OrderModel;

public interface OrdersBusinessServiceInterface {

	public void init();

	public void destroy();

	public void test();

	public List<OrderModel> getOrders();
	
	public OrderModel getOrderById(String id);
}
