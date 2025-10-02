package com.jellyone.oscars.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.io.IOException;

/**
 * Конфигурация для обслуживания статических файлов фронтенда
 * Настраивает маршрутизацию для Single Page Application (SPA)
 */
@Slf4j
@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        log.info("Configuring static resource handlers for frontend");

        // Обслуживание статических файлов Next.js
        registry
                .addResourceHandler("/_next/**")
                .addResourceLocations("classpath:/static/_next/")
                .setCachePeriod(31536000); // Кэш на год для статических ресурсов

        // Обслуживание favicon и других корневых файлов
        registry
                .addResourceHandler("/favicon.ico", "/index.txt")
                .addResourceLocations("classpath:/static/")
                .setCachePeriod(86400); // Кэш на день

        // Обслуживание страницы 404
        registry
                .addResourceHandler("/404/**")
                .addResourceLocations("classpath:/static/404/")
                .setCachePeriod(3600); // Кэш на час

        // Основной обработчик для SPA - все остальные маршруты направляет на index.html
        // Исключаем API маршруты (/api/**, /oscars/**, /swagger-ui/**, /v3/api-docs/**)
        registry
                .addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .setCachePeriod(0) // Не кэшируем HTML файлы
                .resourceChain(true)
                .addResolver(new PathResourceResolver() {
                    @Override
                    protected Resource getResource(@NonNull String resourcePath, @NonNull Resource location) throws IOException {
                        Resource requestedResource = location.createRelative(resourcePath);
                        
                        // Если запрашиваемый ресурс существует, возвращаем его
                        if (requestedResource.exists() && requestedResource.isReadable()) {
                            return requestedResource;
                        }
                        
                        // Для SPA маршрутизации - если файл не найден и это не API запрос,
                        // возвращаем index.html
                        if (!resourcePath.startsWith("api/") && 
                            !resourcePath.startsWith("oscars/") &&
                            !resourcePath.startsWith("swagger-ui/") &&
                            !resourcePath.startsWith("v3/api-docs/") &&
                            !resourcePath.startsWith("_next/") &&
                            !resourcePath.contains(".")) {
                            
                            Resource indexResource = location.createRelative("index.html");
                            if (indexResource.exists() && indexResource.isReadable()) {
                                log.debug("Serving index.html for SPA route: {}", resourcePath);
                                return indexResource;
                            }
                        }
                        
                        return null;
                    }
                });

        log.info("Static resource handlers configured successfully");
    }
}
