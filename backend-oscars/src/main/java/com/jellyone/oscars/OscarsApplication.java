package com.jellyone.oscars;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication(exclude = {
    ValidationAutoConfiguration.class, 
    WebMvcAutoConfiguration.class
})
public class OscarsApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(OscarsApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(OscarsApplication.class);
    }
}


