package com.gcu.topic04.data;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.gcu.topic04.data.entity.OrderEntity;
import com.gcu.topic04.data.repository.OrdersRepository;

import javax.sql.DataSource;

@Service
public class OrdersDataService {

	@Autowired
	private OrdersRepository ordersRepository;
	@SuppressWarnings("unused")
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplateObject;

	public OrdersDataService(OrdersRepository ordersRepository, DataSource dataSource) {
		this.ordersRepository = ordersRepository;
		this.dataSource = dataSource;
		this.jdbcTemplateObject = new JdbcTemplate(dataSource);
	}

	public OrdersDataService(OrdersRepository ordersRepository) {
		this.ordersRepository = ordersRepository;
	}

	public OrdersDataService() {
		super();
	}

	public List findAll() {

		List<OrderEntity> orders = new ArrayList<>();

		try {
			// Get all of the Entity Orders
			Iterable<OrderEntity> ordersIterable = ordersRepository.findAll();

			// Convert to a List and return the List
			orders = new ArrayList<OrderEntity>();
			ordersIterable.forEach(orders::add);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return orders;

	}

	public Object findById(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean update(Object t) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean delete(Object t) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean create(OrderEntity order) {

		String sql = "INSERT INTO ORDERS(ORDER_NO, PRODUCT_NAME, PRICE, QUANTITY) VALUES(?, ?, ?, ?)";

		// Execute SQL Insert
		try {

			jdbcTemplateObject.update(sql, order.getOrderNo(), order.getProductName(), order.getPrice(),
					order.getQuantity());
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

}
