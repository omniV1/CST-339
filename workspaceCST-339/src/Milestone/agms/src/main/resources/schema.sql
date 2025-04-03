-- Drop tables if they exist (for clean restart)
DROP TABLE IF EXISTS maintenance_records;
DROP TABLE IF EXISTS flights;
DROP TABLE IF EXISTS aircraft;
DROP TABLE IF EXISTS gates;
DROP TABLE IF EXISTS users;

-- Create users table with only the minimal fields needed
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL
);

-- Insert admin user with encoded password (password: 'password')
INSERT INTO users (username, password, role)
VALUES ('admin', '$2a$10$T7Pnj7Y0aJEYmKXQnWPSU.DYIE0zK4tfIxGqyQiP7kIuajoVLqYAG', 'ADMIN');

-- Create other necessary tables
CREATE TABLE aircraft (
    registration_number VARCHAR(10) PRIMARY KEY,
    model VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL,
    capacity INT NOT NULL,
    year_manufactured INT,
    airline_code VARCHAR(3),
    current_location VARCHAR(3),
    last_maintenance_date TIMESTAMP
);

CREATE TABLE gates (
    gate_id VARCHAR(5) PRIMARY KEY,
    terminal VARCHAR(2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    type VARCHAR(20),
    last_service_date TIMESTAMP
);

CREATE TABLE flights (
    flight_number VARCHAR(10) PRIMARY KEY,
    airline_code VARCHAR(3) NOT NULL,
    origin VARCHAR(3) NOT NULL,
    destination VARCHAR(3) NOT NULL,
    assigned_aircraft VARCHAR(10),
    assigned_gate VARCHAR(5),
    scheduled_departure TIMESTAMP NOT NULL,
    scheduled_arrival TIMESTAMP NOT NULL,
    actual_departure TIMESTAMP,
    actual_arrival TIMESTAMP,
    status VARCHAR(20) NOT NULL,
    FOREIGN KEY (assigned_aircraft) REFERENCES aircraft(registration_number),
    FOREIGN KEY (assigned_gate) REFERENCES gates(gate_id)
);

CREATE TABLE maintenance_records (
    id INT AUTO_INCREMENT PRIMARY KEY,
    registration_number VARCHAR(10) NOT NULL,
    scheduled_date TIMESTAMP NOT NULL,
    completed_date TIMESTAMP,
    type VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL,
    description TEXT,
    technician VARCHAR(50),
    FOREIGN KEY (registration_number) REFERENCES aircraft(registration_number)
);

-- Insert sample data for aircraft
INSERT INTO aircraft (registration_number, model, status, capacity, year_manufactured, airline_code, current_location)
VALUES 
    ('N12345', 'Boeing 737-800', 'ACTIVE', 189, 2018, 'AAL', 'PHX'),
    ('N54321', 'Airbus A320', 'MAINTENANCE', 180, 2016, 'DAL', 'ATL'),
    ('N98765', 'Boeing 787-9', 'ACTIVE', 290, 2020, 'UAL', 'DEN'),
    ('N56789', 'Airbus A350-900', 'ACTIVE', 325, 2019, 'AAL', 'DFW'),
    ('N13579', 'Embraer E190', 'INACTIVE', 100, 2015, 'JBU', 'BOS');

-- Insert sample data for gates
INSERT INTO gates (gate_id, terminal, status, type)
VALUES 
    ('A1', 'A', 'AVAILABLE', 'DOMESTIC'),
    ('A2', 'A', 'OCCUPIED', 'DOMESTIC'),
    ('B3', 'B', 'AVAILABLE', 'INTERNATIONAL'),
    ('B4', 'B', 'MAINTENANCE', 'INTERNATIONAL'),
    ('C5', 'C', 'AVAILABLE', 'DOMESTIC');

-- Insert sample data for flights
INSERT INTO flights (flight_number, airline_code, origin, destination, assigned_aircraft, assigned_gate, 
                    scheduled_departure, scheduled_arrival, status)
VALUES 
    ('AA100', 'AAL', 'PHX', 'LAX', 'N12345', 'A1', '2023-04-15 08:00:00', '2023-04-15 09:30:00', 'SCHEDULED'),
    ('DL200', 'DAL', 'ATL', 'JFK', NULL, 'A2', '2023-04-15 10:15:00', '2023-04-15 12:45:00', 'SCHEDULED'),
    ('UA300', 'UAL', 'DEN', 'SFO', 'N98765', 'B3', '2023-04-15 11:30:00', '2023-04-15 13:15:00', 'BOARDING'),
    ('AA400', 'AAL', 'DFW', 'MIA', 'N56789', NULL, '2023-04-15 14:00:00', '2023-04-15 17:30:00', 'DELAYED'),
    ('B6500', 'JBU', 'BOS', 'LAS', NULL, NULL, '2023-04-15 16:45:00', '2023-04-15 20:15:00', 'SCHEDULED');

-- Insert sample data for maintenance records
INSERT INTO maintenance_records (registration_number, scheduled_date, type, status, description)
VALUES 
    ('N54321', '2023-04-16 09:00:00', 'ROUTINE', 'SCHEDULED', 'Regular maintenance check'),
    ('N12345', '2023-04-20 08:30:00', 'ENGINE', 'SCHEDULED', 'Engine inspection'),
    ('N98765', '2023-04-18 14:00:00', 'AVIONICS', 'SCHEDULED', 'Avionics system update'),
    ('N56789', '2023-04-17 10:00:00', 'ROUTINE', 'SCHEDULED', 'Regular maintenance check'),
    ('N54321', '2023-03-15 09:00:00', 'ROUTINE', 'COMPLETED', 'Previous maintenance'); 