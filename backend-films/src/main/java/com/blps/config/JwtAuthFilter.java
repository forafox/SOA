package com.blps.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.Date;

@Provider
@Slf4j
public class JwtAuthFilter implements ContainerRequestFilter {
    
    private static final String SECRET_KEY = "mySecretKey123456789012345678901234567890";
    private static final String BEARER_PREFIX = "Bearer ";
    
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String path = requestContext.getUriInfo().getPath();
        
        // Skip JWT validation for SAML authentication endpoints
        if (path.contains("/saml") || path.contains("/login") || path.contains("/logout")) {
            return;
        }
        
        String authHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
        
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            log.warn("No JWT token found in request to {}", path);
            return; // Let SAML filter handle authentication
        }
        
        String token = authHeader.substring(BEARER_PREFIX.length());
        
        try {
            if (validateToken(token)) {
                log.info("JWT token validated successfully for inter-service communication");
                // Add user info to request context for downstream processing
                Claims claims = extractAllClaims(token);
                requestContext.setProperty("jwt.user", claims.getSubject());
                requestContext.setProperty("jwt.service", claims.get("service"));
            } else {
                log.warn("Invalid JWT token for inter-service communication");
                requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Invalid JWT token").build());
            }
        } catch (Exception e) {
            log.error("Error validating JWT token", e);
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                .entity("JWT validation failed").build());
        }
    }
    
    private boolean validateToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return !isTokenExpired(claims);
        } catch (Exception e) {
            log.error("Token validation failed", e);
            return false;
        }
    }
    
    private Claims extractAllClaims(String token) {
        SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    
    private boolean isTokenExpired(Claims claims) {
        return claims.getExpiration().before(new Date());
    }
}
