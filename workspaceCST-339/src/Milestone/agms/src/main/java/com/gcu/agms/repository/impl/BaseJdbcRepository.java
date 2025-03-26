package com.gcu.agms.repository.impl;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

/**
 * Base class for JDBC repository implementations.
 * This class provides common functionality for JDBC repository implementations,
 * reducing code duplication and standardizing error handling.
 *
 * @param <T> The entity type
 * @param <ID> The ID type
 */
public abstract class BaseJdbcRepository<T, ID> {
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    protected final JdbcTemplate jdbcTemplate;
    
    /**
     * Constructor with JdbcTemplate dependency injection.
     * 
     * @param jdbcTemplate The JDBC template for database operations
     */
    public BaseJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        logger.info("Initialized {} with JdbcTemplate", getClass().getSimpleName());
    }
    
    /**
     * Executes a query and returns a list of results.
     * 
     * @param sql The SQL query to execute
     * @param rowMapper The row mapper to convert rows to objects
     * @param args The query arguments
     * @return A list of query results
     */
    protected List<T> executeQuery(String sql, RowMapper<T> rowMapper, Object... args) {
        try {
            return jdbcTemplate.query(sql, rowMapper, args);
        } catch (DataAccessException e) {
            logger.error("Database error executing query: {}", e.getMessage(), e);
            return List.of();
        }
    }
    
    /**
     * Executes a query and returns a single optional result.
     * 
     * @param sql The SQL query to execute
     * @param rowMapper The row mapper to convert rows to objects
     * @param args The query arguments
     * @return An optional containing the result if found, empty optional otherwise
     */
    protected Optional<T> executeFindOne(String sql, RowMapper<T> rowMapper, Object... args) {
        try {
            List<T> results = jdbcTemplate.query(sql, rowMapper, args);
            return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
        } catch (EmptyResultDataAccessException e) {
            logger.debug("No result found for query");
            return Optional.empty();
        } catch (DataAccessException e) {
            logger.error("Database error executing query: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }
    
    /**
     * Executes an update statement.
     * 
     * @param sql The SQL update statement
     * @param args The update arguments
     * @return The number of rows affected
     */
    protected int executeUpdate(String sql, Object... args) {
        try {
            return jdbcTemplate.update(sql, args);
        } catch (DataAccessException e) {
            logger.error("Database error executing update: {}", e.getMessage(), e);
            return 0;
        }
    }
    
    /**
     * Executes a count query.
     * 
     * @param sql The SQL count query
     * @param args The query arguments
     * @return The count result
     */
    protected int executeCount(String sql, Object... args) {
        try {
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, args);
            return count != null ? count : 0;
        } catch (DataAccessException e) {
            logger.error("Database error executing count: {}", e.getMessage(), e);
            return 0;
        }
    }
    
    /**
     * Checks if a row exists.
     * 
     * @param sql The SQL existence query
     * @param args The query arguments
     * @return true if a row exists, false otherwise
     */
    protected boolean executeExists(String sql, Object... args) {
        try {
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, args);
            return count != null && count > 0;
        } catch (DataAccessException e) {
            logger.error("Database error checking existence: {}", e.getMessage(), e);
            return false;
        }
    }
}