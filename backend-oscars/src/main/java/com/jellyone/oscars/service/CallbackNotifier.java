package com.jellyone.oscars.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CallbackNotifier {
    private final RestTemplate restTemplate;

    public void postJson(String callbackUrl, Map<String, Object> payload) {
        if (callbackUrl == null || callbackUrl.isBlank()) {
            log.warn("CallbackNotifier: callbackUrl is empty; skipping callback");
            return;
        }
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(callbackUrl, request, String.class);
            log.info("CallbackNotifier: POST {} -> status {}", callbackUrl, response.getStatusCode());
        } catch (RestClientException e) {
            log.error("CallbackNotifier: Failed to POST {}: {}", callbackUrl, e.getMessage());
        } catch (Exception e) {
            log.error("CallbackNotifier: Unexpected error POST {}: {}", callbackUrl, e.getMessage(), e);
        }
    }
}


