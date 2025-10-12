package com.jellyone.oscars.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@Slf4j
public class MoviesApiClient {
    
    @Autowired
    private JwtService jwtService;
    
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String moviesApiUrl = "http://localhost:8080/api";
    
    public <T> T get(String endpoint, Class<T> responseType) {
        return makeRequest(HttpMethod.GET, endpoint, null, responseType);
    }
    
    public <T> T post(String endpoint, Object requestBody, Class<T> responseType) {
        return makeRequest(HttpMethod.POST, endpoint, requestBody, responseType);
    }
    
    public <T> T patch(String endpoint, Object requestBody, Class<T> responseType) {
        return makeRequest(HttpMethod.PATCH, endpoint, requestBody, responseType);
    }
    
    public void delete(String endpoint) {
        makeRequest(HttpMethod.DELETE, endpoint, null, Void.class);
    }
    
    private <T> T makeRequest(HttpMethod method, String endpoint, Object requestBody, Class<T> responseType) {
        try {
            String url = moviesApiUrl + endpoint;
            
            // Generate JWT token for inter-service communication
            String token = jwtService.generateToken("backend-oscars", "oscars-service");
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            headers.set("Content-Type", "application/json");
            
            HttpEntity<?> entity = new HttpEntity<>(requestBody, headers);
            
            log.info("Making {} request to {} with JWT token", method, url);
            
            ResponseEntity<T> response = restTemplate.exchange(url, method, entity, responseType);
            
            log.info("Response received with status: {}", response.getStatusCode());
            
            return response.getBody();
            
        } catch (Exception e) {
            log.error("Error making request to movies API: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to communicate with movies API", e);
        }
    }
}
