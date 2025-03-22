package com.gcu.topic04.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gcu.topic04.data.entity.OrderEntity;
import com.gcu.topic04.data.repository.OrdersRepository;

@Service
public class OrdersDataService implements DataAccessInterface<OrderEntity> {

    @Autowired
    private OrdersRepository ordersRepository;

    public OrdersDataService(OrdersRepository ordersRepository) {
        this.ordersRepository = ordersRepository;
    }

    @Override
    public OrderEntity findById(String id) {
        Optional<OrderEntity> order = ordersRepository.findById(id);
        return order.orElse(null);
    }

    @Override
    public List<OrderEntity> findAll() {
        List<OrderEntity> orders = new ArrayList<OrderEntity>();

        try {
            // Use Spring Data MongoDB's findAll() method
            Iterable<OrderEntity> ordersIterable = ordersRepository.findAll();
            orders = new ArrayList<OrderEntity>();
            ordersIterable.forEach(orders::add);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return orders;
    }

    @Override
    public OrderEntity create(OrderEntity order) {
        try {
            // MongoRepository.save() returns the saved entity
            return ordersRepository.save(order);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean update(OrderEntity order) {
        try {
            ordersRepository.save(order);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(OrderEntity order) {
        try {
            ordersRepository.delete(order);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}