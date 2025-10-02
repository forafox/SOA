package com.jellyone.oscars.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.boot.logging.LoggingSystem;

import jakarta.annotation.PostConstruct;
import java.util.logging.LogManager;

@Configuration
@Profile("wildfly")
public class WildFlyLoggingConfig {
    
    @PostConstruct
    public void configureLogging() {
        // Устанавливаем JBoss LogManager как основной
        System.setProperty("java.util.logging.manager", "org.jboss.logmanager.LogManager");
        
        // Отключаем автоконфигурацию Spring Boot логирования
        System.setProperty("logging.config", "");
        
        // Активируем профиль wildfly если не установлен
        String activeProfiles = System.getProperty("spring.profiles.active");
        if (activeProfiles == null || activeProfiles.isEmpty()) {
            System.setProperty("spring.profiles.active", "wildfly");
        }
        
        // Принудительно используем JBoss LogManager
        LogManager.getLogManager();
        
        System.out.println("=== WildFly Logging Configuration Applied ===");
        System.out.println("Active profiles: " + System.getProperty("spring.profiles.active"));
        System.out.println("Log manager: " + System.getProperty("java.util.logging.manager"));
    }
}
