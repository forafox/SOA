package com.jellyone.oscars.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/debug")
public class DebugController {

    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> debugTest(HttpServletRequest request) {
        log.info("=== DEBUG: /debug/test called ===");
        log.info("Request URI: {}", request.getRequestURI());
        log.info("Context Path: {}", request.getContextPath());
        log.info("Servlet Path: {}", request.getServletPath());
        log.info("Request URL: {}", request.getRequestURL());
        
        return ResponseEntity.ok(Map.of(
                "message", "Debug controller works!",
                "requestURI", request.getRequestURI(),
                "contextPath", request.getContextPath(),
                "servletPath", request.getServletPath()
        ));
    }

    @GetMapping("/api/test")
    public ResponseEntity<Map<String, Object>> debugApiTest(HttpServletRequest request) {
        log.info("=== DEBUG: /debug/api/test called ===");
        log.info("Request URI: {}", request.getRequestURI());
        log.info("Context Path: {}", request.getContextPath());
        
        return ResponseEntity.ok(Map.of(
                "message", "Debug API test works!",
                "requestURI", request.getRequestURI(),
                "contextPath", request.getContextPath()
        ));
    }
}
