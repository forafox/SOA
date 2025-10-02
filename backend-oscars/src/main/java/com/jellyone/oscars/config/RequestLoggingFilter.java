package com.jellyone.oscars.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;

@Slf4j
@Component
@Order(1)
public class RequestLoggingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        if (request instanceof HttpServletRequest httpRequest) {
            String requestURI = httpRequest.getRequestURI();
            String method = httpRequest.getMethod();
            String contextPath = httpRequest.getContextPath();
            
            log.info("=== REQUEST FILTER: {} {} ===", method, requestURI);
            log.info("Context Path: {}", contextPath);
            log.info("Query String: {}", httpRequest.getQueryString());
            
            // Особое внимание к API запросам
            if (requestURI.contains("/api/")) {
                log.warn("=== API REQUEST DETECTED: {} {} ===", method, requestURI);
            }
        }
        
        chain.doFilter(request, response);
    }
}
