package com.gcu.agms.repository.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import com.gcu.agms.model.flight.FlightModel;
import com.gcu.agms.repository.FlightRepository;

/**
 * JDBC implementation of the FlightRepository interface.
 * This class handles data access operations for flights using Spring JDBC.
 */
@Repository
public class JdbcFlightRepository implements FlightRepository {

    private static final Logger logger = LoggerFactory.getLogger(JdbcFlightRepository.class);
    private final JdbcTemplate jdbcTemplate;
    
    /**
     * Constructor with JdbcTemplate dependency injection.
     * @param jdbcTemplate The JDBC template for database operations
     */
    public JdbcFlightRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        logger.info("Initialized JdbcFlightRepository");
    }
    
    @Override
    public List<FlightModel> findAll() {
        logger.debug("Finding all flights");
        String sql = "SELECT * FROM flight ORDER BY scheduled_departure";
        
        try {
            return jdbcTemplate.query(sql, new FlightRowMapper());
        } catch (DataAccessException e) {
            logger.error("Database error finding all flights: {}", e.getMessage(), e);
            return List.of();
        }
    }
    
    @Override
    public Optional<FlightModel> findById(Long id) {
        logger.debug("Finding flight by ID: {}", id);
        String sql = "SELECT * FROM flight WHERE id = ?";
        
        try {
            List<FlightModel> results = jdbcTemplate.query(sql, new FlightRowMapper(), id);
            return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
        } catch (EmptyResultDataAccessException e) {
            logger.debug("No flight found with ID: {}", id);
            return Optional.empty();
        } catch (DataAccessException e) {
            logger.error("Database error finding flight by ID: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }
    
    @Override
    public Optional<FlightModel> findByFlightNumber(String flightNumber) {
        logger.debug("Finding flight by flight number: {}", flightNumber);
        String sql = "SELECT * FROM flight WHERE flight_number = ?";
        
        try {
            List<FlightModel> results = jdbcTemplate.query(sql, new FlightRowMapper(), flightNumber);
            return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
        } catch (EmptyResultDataAccessException e) {
            logger.debug("No flight found with flight number: {}", flightNumber);
            return Optional.empty();
        } catch (DataAccessException e) {
            logger.error("Database error finding flight by flight number: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }
    
    @Override
    public List<FlightModel> findActiveFlights() {
        logger.debug("Finding active flights");
        String sql = "SELECT * FROM flight WHERE status NOT IN ('COMPLETED', 'CANCELLED') " +
                     "ORDER BY scheduled_departure";
        
        try {
            return jdbcTemplate.query(sql, new FlightRowMapper());
        } catch (DataAccessException e) {
            logger.error("Database error finding active flights: {}", e.getMessage(), e);
            return List.of();
        }
    }
    
    @Override
    public List<FlightModel> findByStatus(String status) {
        logger.debug("Finding flights by status: {}", status);
        String sql = "SELECT * FROM flight WHERE status = ? ORDER BY scheduled_departure";
        
        try {
            return jdbcTemplate.query(sql, new FlightRowMapper(), status);
        } catch (DataAccessException e) {
            logger.error("Database error finding flights by status: {}", e.getMessage(), e);
            return List.of();
        }
    }
    
    @Override
    public List<FlightModel> findByOrigin(String origin) {
        logger.debug("Finding flights by origin: {}", origin);
        String sql = "SELECT * FROM flight WHERE origin = ? ORDER BY scheduled_departure";
        
        try {
            return jdbcTemplate.query(sql, new FlightRowMapper(), origin);
        } catch (DataAccessException e) {
            logger.error("Database error finding flights by origin: {}", e.getMessage(), e);
            return List.of();
        }
    }
    
    @Override
    public List<FlightModel> findByDestination(String destination) {
        logger.debug("Finding flights by destination: {}", destination);
        String sql = "SELECT * FROM flight WHERE destination = ? ORDER BY scheduled_departure";
        
        try {
            return jdbcTemplate.query(sql, new FlightRowMapper(), destination);
        } catch (DataAccessException e) {
            logger.error("Database error finding flights by destination: {}", e.getMessage(), e);
            return List.of();
        }
    }
    
    @Override
    public List<FlightModel> findByAirlineCode(String airlineCode) {
        logger.debug("Finding flights by airline code: {}", airlineCode);
        String sql = "SELECT * FROM flight WHERE airline_code = ? ORDER BY scheduled_departure";
        
        try {
            return jdbcTemplate.query(sql, new FlightRowMapper(), airlineCode);
        } catch (DataAccessException e) {
            logger.error("Database error finding flights by airline code: {}", e.getMessage(), e);
            return List.of();
        }
    }
    
    @Override
    public List<FlightModel> findByAssignedAircraft(String aircraftRegistration) {
        logger.debug("Finding flights by assigned aircraft: {}", aircraftRegistration);
        String sql = "SELECT * FROM flight WHERE assigned_aircraft = ? ORDER BY scheduled_departure";
        
        try {
            return jdbcTemplate.query(sql, new FlightRowMapper(), aircraftRegistration);
        } catch (DataAccessException e) {
            logger.error("Database error finding flights by assigned aircraft: {}", e.getMessage(), e);
            return List.of();
        }
    }
    
    @Override
    public FlightModel save(FlightModel flight) {
        if (flight.getId() == null) {
            // Insert new flight
            return insertFlight(flight);
        } else {
            // Update existing flight
            return updateFlight(flight);
        }
    }
    
    private FlightModel insertFlight(FlightModel flight) {
        logger.debug("Inserting new flight: {}", flight.getFlightNumber());
        
        String sql = "INSERT INTO flight (flight_number, airline_code, origin, destination, "
                   + "scheduled_departure, scheduled_arrival, actual_departure, actual_arrival, "
                   + "assigned_aircraft, current_location, status, departure_gate, arrival_gate, "
                   + "route, passenger_count, remarks, created_at, updated_at) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
                ps.setString(1, flight.getFlightNumber());
                ps.setString(2, flight.getAirlineCode());
                ps.setString(3, flight.getOrigin());
                ps.setString(4, flight.getDestination());
                ps.setTimestamp(5, Timestamp.valueOf(flight.getScheduledDeparture()));
                ps.setTimestamp(6, Timestamp.valueOf(flight.getScheduledArrival()));
                
                // Handle nullable time fields
                if (flight.getActualDeparture() != null) {
                    ps.setTimestamp(7, Timestamp.valueOf(flight.getActualDeparture()));
                } else {
                    ps.setNull(7, java.sql.Types.TIMESTAMP);
                }
                
                if (flight.getActualArrival() != null) {
                    ps.setTimestamp(8, Timestamp.valueOf(flight.getActualArrival()));
                } else {
                    ps.setNull(8, java.sql.Types.TIMESTAMP);
                }
                
                ps.setString(9, flight.getAssignedAircraft());
                ps.setString(10, flight.getCurrentLocation());
                ps.setString(11, flight.getStatus().name());
                ps.setString(12, flight.getDepartureGate());
                ps.setString(13, flight.getArrivalGate());
                ps.setString(14, flight.getRoute());
                
                // Handle nullable numeric fields
                ps.setInt(15, flight.getPassengerCount());
                
                ps.setString(16, flight.getRemarks());
                
                // Set timestamps
                LocalDateTime now = LocalDateTime.now();
                ps.setTimestamp(17, Timestamp.valueOf(now)); // created_at
                ps.setTimestamp(18, Timestamp.valueOf(now)); // updated_at
                
                return ps;
            }, keyHolder);
            
            Number key = keyHolder.getKey();
            if (key != null) {
                flight.setId(key.longValue());
            }
            
        } catch (DataAccessException e) {
            logger.error("Database error inserting flight: {}", e.getMessage(), e);
        }
        
        return flight;
    }
    
    private FlightModel updateFlight(FlightModel flight) {
        logger.debug("Updating flight: {}", flight.getFlightNumber());
        
        String sql = "UPDATE flight SET flight_number = ?, airline_code = ?, origin = ?, "
                   + "destination = ?, scheduled_departure = ?, scheduled_arrival = ?, "
                   + "actual_departure = ?, actual_arrival = ?, assigned_aircraft = ?, "
                   + "current_location = ?, status = ?, departure_gate = ?, arrival_gate = ?, "
                   + "route = ?, passenger_count = ?, remarks = ?, updated_at = ? WHERE id = ?";
        
        try {
            LocalDateTime now = LocalDateTime.now();
            
            jdbcTemplate.update(
                sql,
                flight.getFlightNumber(),
                flight.getAirlineCode(),
                flight.getOrigin(),
                flight.getDestination(),
                Timestamp.valueOf(flight.getScheduledDeparture()),
                Timestamp.valueOf(flight.getScheduledArrival()),
                flight.getActualDeparture() != null ? Timestamp.valueOf(flight.getActualDeparture()) : null,
                flight.getActualArrival() != null ? Timestamp.valueOf(flight.getActualArrival()) : null,
                flight.getAssignedAircraft(),
                flight.getCurrentLocation(),
                flight.getStatus().name(),
                flight.getDepartureGate(),
                flight.getArrivalGate(),
                flight.getRoute(),
                flight.getPassengerCount(),
                flight.getRemarks(),
                Timestamp.valueOf(now),
                flight.getId()
            );
        } catch (DataAccessException e) {
            logger.error("Database error updating flight: {}", e.getMessage(), e);
        }
        
        return flight;
    }
    
    @Override
    public void deleteById(Long id) {
        logger.debug("Deleting flight with ID: {}", id);
        
        String sql = "DELETE FROM flight WHERE id = ?";
        
        try {
            jdbcTemplate.update(sql, id);
        } catch (DataAccessException e) {
            logger.error("Database error deleting flight: {}", e.getMessage(), e);
        }
    }
    
    @Override
    public boolean deleteByFlightNumber(String flightNumber) {
        logger.debug("Deleting flight with flight number: {}", flightNumber);
        
        String sql = "DELETE FROM flight WHERE flight_number = ?";
        
        try {
            int rowsAffected = jdbcTemplate.update(sql, flightNumber);
            return rowsAffected > 0;
        } catch (DataAccessException e) {
            logger.error("Database error deleting flight by flight number: {}", e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public boolean existsByFlightNumber(String flightNumber) {
        logger.debug("Checking if flight exists with flight number: {}", flightNumber);
        String sql = "SELECT COUNT(*) FROM flight WHERE flight_number = ?";
        
        try {
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, flightNumber);
            return count != null && count > 0;
        } catch (DataAccessException e) {
            logger.error("Database error checking if flight exists: {}", e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public int countByStatus(String status) {
        logger.debug("Counting flights by status: {}", status);
        String sql = "SELECT COUNT(*) FROM flight WHERE status = ?";
        
        try {
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, status);
            return count != null ? count : 0;
        } catch (DataAccessException e) {
            logger.error("Database error counting flights by status: {}", e.getMessage(), e);
            return 0;
        }
    }
    
    @Override
    public int countActiveFlights() {
        logger.debug("Counting active flights");
        String sql = "SELECT COUNT(*) FROM flight WHERE status NOT IN ('COMPLETED', 'CANCELLED')";
        
        try {
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
            return count != null ? count : 0;
        } catch (DataAccessException e) {
            logger.error("Database error counting active flights: {}", e.getMessage(), e);
            return 0;
        }
    }
    
    @Override
    public int countDelayedFlights() {
        logger.debug("Counting delayed flights");
        String sql = "SELECT COUNT(*) FROM flight WHERE status = 'DELAYED'";
        
        try {
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
            return count != null ? count : 0;
        } catch (DataAccessException e) {
            logger.error("Database error counting delayed flights: {}", e.getMessage(), e);
            return 0;
        }
    }
    
    @Override
    public int countByStatusNotIn(List<String> statuses) {
        logger.debug("Counting flights with status not in: {}", statuses);
        
        if (statuses == null || statuses.isEmpty()) {
            return countAll();
        }
        
        // Create placeholders for SQL IN clause
        String placeholders = statuses.stream()
            .map(s -> "?")
            .collect(Collectors.joining(", "));
        
        String sql = String.format("SELECT COUNT(*) FROM flight WHERE status NOT IN (%s)", placeholders);
        
        try {
            Integer count = jdbcTemplate.queryForObject(
                sql, 
                Integer.class,
                statuses.toArray()
            );
            return count != null ? count : 0;
        } catch (DataAccessException e) {
            logger.error("Database error counting flights by status not in: {}", e.getMessage(), e);
            return 0;
        }
    }
    
    @Override
    public int countAll() {
        logger.debug("Counting all flights");
        String sql = "SELECT COUNT(*) FROM flight";
        
        try {
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
            return count != null ? count : 0;
        } catch (DataAccessException e) {
            logger.error("Database error counting all flights: {}", e.getMessage(), e);
            return 0;
        }
    }
    
    /**
     * Row mapper for converting database rows to FlightModel objects.
     */
    private static class FlightRowMapper implements RowMapper<FlightModel> {
        @Override
        public FlightModel mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
            FlightModel flight = new FlightModel();
            flight.setId(rs.getLong("id"));
            flight.setFlightNumber(rs.getString("flight_number"));
            flight.setAirlineCode(rs.getString("airline_code"));
            flight.setOrigin(rs.getString("origin"));
            flight.setDestination(rs.getString("destination"));
            
            // Handle timestamps
            flight.setScheduledDeparture(rs.getTimestamp("scheduled_departure").toLocalDateTime());
            flight.setScheduledArrival(rs.getTimestamp("scheduled_arrival").toLocalDateTime());
            
            Timestamp actualDepartureTimestamp = rs.getTimestamp("actual_departure");
            if (actualDepartureTimestamp != null) {
                flight.setActualDeparture(actualDepartureTimestamp.toLocalDateTime());
            }
            
            Timestamp actualArrivalTimestamp = rs.getTimestamp("actual_arrival");
            if (actualArrivalTimestamp != null) {
                flight.setActualArrival(actualArrivalTimestamp.toLocalDateTime());
            }
            
            flight.setAssignedAircraft(rs.getString("assigned_aircraft"));
            flight.setCurrentLocation(rs.getString("current_location"));
            
            // Handle enum
            String statusStr = rs.getString("status");
            try {
                flight.setStatus(FlightModel.FlightStatus.valueOf(statusStr));
            } catch (IllegalArgumentException e) {
                // Default to SCHEDULED if status is invalid
                flight.setStatus(FlightModel.FlightStatus.SCHEDULED);
            }
            
            flight.setDepartureGate(rs.getString("departure_gate"));
            flight.setArrivalGate(rs.getString("arrival_gate"));
            flight.setRoute(rs.getString("route"));
            flight.setPassengerCount(rs.getInt("passenger_count"));
            flight.setRemarks(rs.getString("remarks"));
            
            return flight;
        }
    }
}