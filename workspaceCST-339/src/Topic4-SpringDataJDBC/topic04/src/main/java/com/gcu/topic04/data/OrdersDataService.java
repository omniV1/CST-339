package com.gcu.topic04.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.gcu.topic04.data.entity.OrderEntity;
import com.gcu.topic04.data.repository.OrdersRepository;

import javax.sql.DataSource;

@Service
public class OrdersDataService implements DataAccessInterface<OrderEntity> {

    private static final Logger logger = LoggerFactory.getLogger(OrdersDataService.class);
    
    @Autowired
    private OrdersRepository ordersRepository;
    
    private DataSource dataSource;
    private JdbcTemplate jdbcTemplateObject;

    /**
     * Constructor with repository and data source injection
     */
    public OrdersDataService(OrdersRepository ordersRepository, DataSource dataSource) {
        this.ordersRepository = ordersRepository;
        this.dataSource = dataSource;
        this.jdbcTemplateObject = new JdbcTemplate(dataSource);
    }

    /**
     * Constructor with only repository injection
     */
    public OrdersDataService(OrdersRepository ordersRepository) {
        this.ordersRepository = ordersRepository;
    }

    /**
     * Default constructor
     */
    public OrdersDataService() {
        super();
    }

    /**
     * Find all orders
     */
    @Override
    public List<OrderEntity> findAll() {
        List<OrderEntity> orders = new ArrayList<>();

        try {
            // Get all of the Entity Orders using the Repository
            Iterable<OrderEntity> ordersIterable = ordersRepository.findAll();

            // Convert to a List and return the List
            orders = new ArrayList<OrderEntity>();
            ordersIterable.forEach(orders::add);
        } catch (Exception e) {
            // Replace printStackTrace with proper logging
            logger.error("Exception in findAll(): ", e);
        }

        return orders;
    }

    /**
     * Find order by ID
     */
    @Override
    public OrderEntity findById(int id) {
        try {
            Optional<OrderEntity> order = ordersRepository.findById((long) id);
            return order.orElse(null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Update an existing order
     */
    @Override
    public boolean update(OrderEntity order) {
        try {
            // Use the CrudRepository to update the order
            ordersRepository.save(order);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Delete an order
     */
    @Override
    public boolean delete(OrderEntity order) {
        try {
            // Use the CrudRepository to delete the order
            ordersRepository.delete(order);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Create a new order using custom JDBC implementation
     */
    @Override
    public boolean create(OrderEntity order) {
        String sql = "INSERT INTO ORDERS(ORDER_NO, PRODUCT_NAME, PRICE, QUANTITY) VALUES(?, ?, ?, ?)";

        // Execute SQL Insert
        try {
            // Using JDBC template instead of repository to demonstrate custom implementation
            jdbcTemplateObject.update(sql, order.getOrderNo(), order.getProductName(), 
                                      order.getPrice(), order.getQuantity());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
