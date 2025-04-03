package cst339.data;

import java.util.List;

import cst339.data.entity.OrderEntity;

public interface DataAccessInterface<T> {

	public List<OrderEntity> findAll();

	public T findById(String id);

	public OrderEntity create(OrderEntity order);

	public boolean update(T t);

	public boolean delete(T t);
}
