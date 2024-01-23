package com.listywave;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class ListywaveApplication {

	public static void main(String[] args) {
		SpringApplication.run(ListywaveApplication.class, args);
	}
}
