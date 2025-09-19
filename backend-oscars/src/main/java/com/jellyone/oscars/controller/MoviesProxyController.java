package com.jellyone.oscars.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class MoviesProxyController {
    
    private final RestTemplate restTemplate;
    private final String moviesApiBaseUrl;
    
    public MoviesProxyController(RestTemplate restTemplate, 
                               @Value("${movies.api.base-url}") String moviesApiBaseUrl) {
        this.restTemplate = restTemplate;
        this.moviesApiBaseUrl = moviesApiBaseUrl;
    }
    
    @GetMapping("/movies/**")
    public ResponseEntity<Object> proxyGet(HttpServletRequest request,
                                         @RequestParam Map<String, String> params) {
        return proxyRequest(request, HttpMethod.GET, null, params);
    }
    
    @PostMapping("/movies/**")
    public ResponseEntity<Object> proxyPost(HttpServletRequest request,
                                          @RequestBody(required = false) Object body) {
        return proxyRequest(request, HttpMethod.POST, body, null);
    }
    
    @PatchMapping("/movies/**")
    public ResponseEntity<Object> proxyPatch(HttpServletRequest request,
                                           @RequestBody(required = false) Object body) {
        return proxyRequest(request, HttpMethod.PATCH, body, null);
    }
    
    @DeleteMapping("/movies/**")
    public ResponseEntity<Object> proxyDelete(HttpServletRequest request) {
        return proxyRequest(request, HttpMethod.DELETE, null, null);
    }
    
    private ResponseEntity<Object> proxyRequest(HttpServletRequest request, 
                                              HttpMethod method, 
                                              Object body, 
                                              Map<String, String> params) {
        // Извлекаем путь после /api/movies/
        String requestPath = request.getRequestURI();
        String path = requestPath.substring("/api/movies".length());
        
        // Строим URL для movies API
        String url = moviesApiBaseUrl + "/movies" + path;
        
        // Добавляем query параметры
        if (params != null && !params.isEmpty()) {
            StringBuilder queryString = new StringBuilder();
            params.forEach((key, value) -> {
                if (queryString.length() > 0) queryString.append("&");
                queryString.append(key).append("=").append(value);
            });
            if (queryString.length() > 0) {
                url += "?" + queryString.toString();
            }
        }
        
        // Копируем заголовки
        HttpHeaders httpHeaders = new HttpHeaders();
        request.getHeaderNames().asIterator().forEachRemaining(headerName -> {
            if (!headerName.equalsIgnoreCase("host")) {
                httpHeaders.add(headerName, request.getHeader(headerName));
            }
        });
        
        HttpEntity<?> entity = new HttpEntity<>(body, httpHeaders);
        
        return restTemplate.exchange(url, method, entity, Object.class);
    }
}
