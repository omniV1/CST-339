package com.gcu.agms;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@DisplayName("AGMS Application Tests")
class AgmsApplicationTest {

    @Test
    @DisplayName("Context loads successfully")
    void contextLoads() {
        // Test that Spring context loads successfully
    }

    @Test
    @DisplayName("Main method starts application")
    void testMainMethod() {
        // Test main method execution
        AgmsApplication.main(new String[]{});
    }
}