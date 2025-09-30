package com.jellyone.oscars.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.validation.Validator;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    
    @Bean
    public Validator mvcValidator() {
        // Создаем пустой валидатор, который ничего не делает
        return new org.springframework.validation.beanvalidation.OptionalValidatorFactoryBean() {
            @Override
            public void afterPropertiesSet() {
                // Не вызываем super.afterPropertiesSet() чтобы избежать classmate
            }
        };
    }
    
    @Bean
    public FormattingConversionService mvcConversionService() {
        // Создаем FormattingConversionService для SpringDoc
        return new FormattingConversionService();
    }
}
