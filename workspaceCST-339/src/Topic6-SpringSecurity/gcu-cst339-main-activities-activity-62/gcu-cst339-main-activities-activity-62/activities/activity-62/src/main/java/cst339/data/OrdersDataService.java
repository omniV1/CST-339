package cst339.data;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cst339.data.entity.OrderEntity;
import cst339.data.repository.OrdersRepository;

@Service
public class OrdersDataService implements DataAccessInterface<Object> {

	@Autowired
	private OrdersRepository ordersRepository;

	public OrdersDataService(OrdersRepository ordersRepository) {
		this.ordersRepository = ordersRepository;
	}

	public OrderEntity findById(String id) {
		return ordersRepository.getOrderById(id);
	}

	public List<OrderEntity> findAll() {
		List<OrderEntity> orders = new ArrayList<OrderEntity>();

		try {
			Iterable<OrderEntity> ordersIterable = ordersRepository.findAll();

			orders = new ArrayList<OrderEntity>();

			ordersIterable.forEach(orders::add);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return orders;
	}

	public OrderEntity create(OrderEntity order) {
		return ordersRepository.save(order);
	}

	@Override
	public boolean update(Object t) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean delete(Object t) {
		// TODO Auto-generated method stub
		return false;
	}

}
