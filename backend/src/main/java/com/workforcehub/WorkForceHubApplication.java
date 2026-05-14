package com.workforcehub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * WorkForceHub - Enterprise Employee Management System
 * Main Spring Boot application entry point.
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableCaching
@EnableAsync
@EnableScheduling
public class WorkForceHubApplication {

    public static void main(String[] args) {
        SpringApplication.run(WorkForceHubApplication.class, args);
    }
}
