package com.jellyone.oscars.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Контроллер для обслуживания фронтенда
 * Обрабатывает все маршруты, которые не являются API запросами
 */
@Slf4j
@Controller
public class FrontendController {

    /**
     * Основной маршрут для SPA - возвращает index.html для всех незарезервированных маршрутов
     */
    @GetMapping(value = {
            "/", "/movies", "/movies/**", "/oscars", "/oscars/**", "/dashboard", "/dashboard/**"
    })
    public String index(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        String contextPath = request.getContextPath();
        
        log.debug("Serving frontend for route: {} (context: {})", requestUri, contextPath);
        
        // Возвращаем forward на index.html для обработки React Router
        return "forward:/index.html";
    }

    /**
     * Явная обработка корневого маршрута
     */
    @GetMapping("/index.html")
    public ResponseEntity<Resource> indexHtml() {
        try {
            Resource resource = new ClassPathResource("static/index.html");
            if (resource.exists() && resource.isReadable()) {
                log.debug("Serving index.html");
                return ResponseEntity.ok()
                        .contentType(MediaType.TEXT_HTML)
                        .body(resource);
            } else {
                log.warn("index.html not found in static resources");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Error serving index.html", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
