package com.jellyone.oscars.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("wildfly")
public class WildFlyLoggingConfig {
    
    // Отключаем автоконфигурацию логирования для WildFly
    // WildFly будет управлять логированием через свой JBoss LogManager
}
