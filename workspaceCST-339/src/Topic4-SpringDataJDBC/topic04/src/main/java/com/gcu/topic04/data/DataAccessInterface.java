package com.gcu.topic04.data;

import java.util.List;

import com.gcu.topic04.data.entity.OrderEntity;

public interface DataAccessInterface<T> {

	public List<T> findAll();

	public T findById(int id);

	public boolean create(OrderEntity order);

	public boolean update(T t);

	public boolean delete(T t);
}
