package com.jellyone.oscars.service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;


@Slf4j
@Component
@RequiredArgsConstructor
public class CallbackNotifier {

    private final WebClient webClient;

    public void postJson(String callbackUrl, Map<String, Object> payload) {
        if (callbackUrl == null || callbackUrl.isBlank()) {
            return;
        }

        try {
            webClient.post()
                    .uri(callbackUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(payload)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .onErrorResume(e -> Mono.empty())
                    .subscribe();
        } catch (Exception e) {
            log.error("Error posting callback url: {}", callbackUrl, e);
        }
    }
}



