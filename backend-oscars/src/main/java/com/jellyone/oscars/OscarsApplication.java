package com.jellyone.oscars;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import jakarta.annotation.PostConstruct;

@Slf4j
@SpringBootApplication(exclude = {
    ValidationAutoConfiguration.class
})
public class OscarsApplication extends SpringBootServletInitializer {

    @PostConstruct
    public void init() {
        log.info("=== Oscars Application Starting ===");
        log.info("Active profiles: {}", System.getProperty("spring.profiles.active"));
        log.info("Java version: {}", System.getProperty("java.version"));
        log.info("Log manager: {}", System.getProperty("java.util.logging.manager"));
        log.info("=== Application Context Initialized ===");
    }

    public static void main(String[] args) {
        log.info("Starting Oscars Application with args: {}", java.util.Arrays.toString(args));
        SpringApplication.run(OscarsApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        log.info("Configuring Spring Boot application for servlet container");
        return builder.sources(OscarsApplication.class);
    }
}


