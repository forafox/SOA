package com.blps.config;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Provider
@Slf4j
public class OAuth2AuthenticationFilter implements ContainerRequestFilter {
    
    private static final String KEYCLOAK_URL = "http://localhost:8082";
    private static final String REALM = "soa-realm";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String path = requestContext.getUriInfo().getPath();
        
        // Skip OAuth2 validation for JWT inter-service communication
        String authHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
            // Check if it's a JWT token (inter-service) or OAuth2 token (user)
            String token = authHeader.substring(BEARER_PREFIX.length());
            if (isJwtToken(token)) {
                log.debug("JWT token detected, skipping OAuth2 validation");
                return;
            }
        }
        
        // Skip OAuth2 validation for public endpoints
        if (path.contains("/health") || path.contains("/actuator")) {
            return;
        }
        
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            log.warn("No OAuth2 token found in request to {}", path);
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                .entity("OAuth2 token required").build());
            return;
        }
        
        String token = authHeader.substring(BEARER_PREFIX.length());
        
        try {
            if (validateOAuth2Token(token)) {
                log.info("OAuth2 token validated successfully");
                // Token is valid, continue with request
            } else {
                log.warn("Invalid OAuth2 token");
                requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Invalid OAuth2 token").build());
            }
        } catch (Exception e) {
            log.error("Error validating OAuth2 token", e);
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                .entity("OAuth2 validation failed").build());
        }
    }
    
    private boolean isJwtToken(String token) {
        // Simple check: JWT tokens have 3 parts separated by dots
        return token.split("\\.").length == 3 && !token.startsWith("eyJ");
    }
    
    private boolean validateOAuth2Token(String token) {
        try {
            String introspectionUrl = String.format("%s/realms/%s/protocol/openid-connect/token/introspect", 
                    KEYCLOAK_URL, REALM);
            
            String formData = "token=" + token + "&client_id=backend-films";
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(introspectionUrl))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(formData))
                    .build();
            
            HttpResponse<String> response = httpClient.send(request, 
                    HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                String responseBody = response.body();
                // Simple check: if response contains "active":true, token is valid
                return responseBody.contains("\"active\":true");
            }
            
            return false;
        } catch (Exception e) {
            log.error("Error during OAuth2 token validation", e);
            return false;
        }
    }
}
