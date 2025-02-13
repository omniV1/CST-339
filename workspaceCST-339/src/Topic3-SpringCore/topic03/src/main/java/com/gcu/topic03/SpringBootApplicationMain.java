package com.gcu.topic03;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan("com.gcu.topic03")

@SpringBootApplication
public class SpringBootApplicationMain {

	public static void main(String[] args) {

		System.out.println("SpringBootApplicationMain before");

		SpringApplication.run(SpringBootApplicationMain.class, args);

		System.out.println("SpringBootApplicationMain after");
	}

}
