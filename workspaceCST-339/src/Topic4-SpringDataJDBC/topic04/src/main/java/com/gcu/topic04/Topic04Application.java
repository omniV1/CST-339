package com.gcu.topic04;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;

@SpringBootApplication(scanBasePackages = "com.gcu.topic04")
@EnableEncryptableProperties
public class Topic04Application {

    public static void main(String[] args) {
        SpringApplication.run(Topic04Application.class, args);
    }

}
