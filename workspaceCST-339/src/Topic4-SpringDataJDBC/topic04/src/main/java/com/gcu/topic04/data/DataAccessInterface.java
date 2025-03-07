package com.gcu.topic04.data;

import java.util.List;

public interface DataAccessInterface<T> {
    /**
     * Find all entities
     * @return List of entities
     */
    public List<T> findAll();
    
    /**
     * Find entity by ID
     * @param id The ID to find
     * @return The entity if found
     */
    public T findById(int id);
    
    /**
     * Create a new entity
     * @param t Entity to create
     * @return True if successful
     */
    public boolean create(T t);
    
    /**
     * Update an existing entity
     * @param t Entity to update
     * @return True if successful
     */
    public boolean update(T t);
    
    /**
     * Delete an entity
     * @param t Entity to delete
     * @return True if successful
     */
    public boolean delete(T t);
}
