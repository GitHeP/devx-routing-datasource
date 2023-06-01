package com.github.devx.routing.integration.springboot;

import org.springframework.boot.SpringApplication;

/**
 * @author Peng He
 * @since 1.0
 */

@org.springframework.boot.autoconfigure.SpringBootApplication(
        scanBasePackages = {"com.github.devx.routing.integration"}
)
@EnableRoutingDataSource
public class SpringBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootApplication.class, args);
    }
}
