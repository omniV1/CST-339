package cst339.data.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import cst339.data.entity.OrderEntity;

public interface OrdersRepository extends MongoRepository<OrderEntity, String> {

	OrderEntity getOrderById(String id);
}
