package com.lp.sql.routing.integration.springboot;

import org.springframework.boot.SpringApplication;

/**
 * @author Peng He
 * @since 1.0
 */

@org.springframework.boot.autoconfigure.SpringBootApplication(
        scanBasePackages = {"com.lp.sql.routing.integration"}
)
@EnableSQLRouting
public class SpringBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootApplication.class, args);
    }
}
