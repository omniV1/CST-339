package com.gcu.agms.config;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

/**
 * Universal database configuration that works in both local and Docker environments.
 * This configuration reads database connection details from environment variables or
 * application properties, making it work seamlessly in different environments.
 * 
 * @author Airport Gate Management System
 * @version 1.0
 */
@Configuration
public class DataSourceConfig {
    private static final Logger logger = LoggerFactory.getLogger(DataSourceConfig.class);

    @Value("${spring.datasource.url:jdbc:mysql://localhost:3306/agms}")
    private String url;

    @Value("${spring.datasource.username:root}")
    private String username;

    @Value("${spring.datasource.password:root}")
    private String password;

    @Value("${spring.datasource.driver-class-name:com.mysql.cj.jdbc.Driver}")
    private String driverClassName;

    /**
     * Creates a DataSource using configuration from environment variables or properties.
     * This will work both locally and in Docker without code changes.
     * 
     * @return Configured DataSource
     */
    @Bean
    public DataSource dataSource() {
        logger.info("Initializing DataSource with URL: {}", url);
        
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        
        // Add connection properties for reliability
        java.util.Properties props = new java.util.Properties();
        props.setProperty("connectTimeout", "30000");
        props.setProperty("socketTimeout", "60000");
        props.setProperty("autoReconnect", "true");
        dataSource.setConnectionProperties(props);
        
        return dataSource;
    }
}