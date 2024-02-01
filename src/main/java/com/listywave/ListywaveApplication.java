package com.listywave;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
@ConfigurationPropertiesScan(basePackages = {"com.listywave"})
public class ListywaveApplication {

    public static void main(String[] args) {
        SpringApplication.run(ListywaveApplication.class, args);
    }
}
