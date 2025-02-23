package com.gcu.agms;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest
class AgmsApplicationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void contextLoads() {
        assertNotNull(applicationContext, "Application context should not be null");
    }

    @Test
    void mainMethodStartsApplication() {
        // Act
        AgmsApplication.main(new String[] {
            "--spring.main.web-application-type=none",
            "--spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration"
        });

        // Assert
        assertNotNull(applicationContext, "Application context should be available after startup");
        assertNotNull(applicationContext.getBean(AgmsApplication.class), 
            "Main application bean should be available");
        assertTrue(applicationContext.getStartupDate() > 0, 
            "Application should have a valid startup timestamp");
    }
}