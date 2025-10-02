package com.jellyone.oscars.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Order(1)
@Tag(name = "Movies Proxy", description = "Прокси для перенаправления запросов к фильмам на внешний сервис")
public class ProxyController {

    private final WebClient webClient;
    
    @Value("${movies.api.proxy-url}")
    private String proxyBaseUrl;

    @GetMapping("/movies/**")
    @Operation(
            summary = "Получить фильмы",
            description = "Прокси-запрос для получения списка фильмов или конкретного фильма",
            operationId = "getMoviesProxy"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешный ответ от внешнего сервиса",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "object"))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Ресурс не найден"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Ошибка прокси-сервера"
            )
    })
    public ResponseEntity<Object> proxyGetMovies(
            HttpServletRequest request,
            @Parameter(description = "Параметры запроса") @RequestParam Map<String, String> queryParams
    ) {
        log.info("=== ProxyController: Received GET request to {} ===", request.getRequestURI());
        return proxyRequest("GET", request, queryParams, null);
    }

    @PostMapping("/movies/**")
    public ResponseEntity<Object> proxyPostMovies(
            HttpServletRequest request,
            @RequestParam Map<String, String> queryParams,
            @RequestBody(required = false) Object body
    ) {
        return proxyRequest("POST", request, queryParams, body);
    }

    @PutMapping("/movies/**")
    public ResponseEntity<Object> proxyPutMovies(
            HttpServletRequest request,
            @RequestParam Map<String, String> queryParams,
            @RequestBody(required = false) Object body
    ) {
        return proxyRequest("PUT", request, queryParams, body);
    }

    @DeleteMapping("/movies/**")
    public ResponseEntity<Object> proxyDeleteMovies(
            HttpServletRequest request,
            @RequestParam Map<String, String> queryParams
    ) {
        return proxyRequest("DELETE", request, queryParams, null);
    }

    @PatchMapping("/movies/**")
    public ResponseEntity<Object> proxyPatchMovies(
            HttpServletRequest request,
            @RequestParam Map<String, String> queryParams,
            @RequestBody(required = false) Object body
    ) {
        return proxyRequest("PATCH", request, queryParams, body);
    }

    private ResponseEntity<Object> proxyRequest(
            String method,
            HttpServletRequest request,
            Map<String, String> queryParams,
            Object body
    ) {
        String requestURI = request.getRequestURI();
        String contextPath = request.getContextPath();
        
        log.info("=== PROXY REQUEST DEBUG ===");
        log.info("Request URI: {}", requestURI);
        log.info("Context Path: {}", contextPath);
        log.info("Method: {}", method);
        
        // Определяем путь для проксирования
        String path;
        if (contextPath != null && !contextPath.isEmpty() && requestURI.startsWith(contextPath)) {
            // Убираем context path, затем /api
            String withoutContext = requestURI.substring(contextPath.length());
            path = withoutContext.substring("/api".length());
        } else {
            // Убираем только /api
            path = requestURI.substring("/api".length());
        }
        
        String targetUrl = buildTargetUrl(path, queryParams);
        
        log.info("Computed path: {}", path);
        log.info("Proxying {} request from {} to {}", method, requestURI, targetUrl);

        try {
            Mono<Object> responseMono;
            
            switch (method.toUpperCase()) {
                case "GET":
                    responseMono = webClient.get()
                            .uri(targetUrl)
                            .retrieve()
                            .bodyToMono(Object.class);
                    break;
                case "POST":
                    if (body != null) {
                        responseMono = webClient.post()
                                .uri(targetUrl)
                                .bodyValue(body)
                                .retrieve()
                                .bodyToMono(Object.class);
                    } else {
                        responseMono = webClient.post()
                                .uri(targetUrl)
                                .retrieve()
                                .bodyToMono(Object.class);
                    }
                    break;
                case "PUT":
                    if (body != null) {
                        responseMono = webClient.put()
                                .uri(targetUrl)
                                .bodyValue(body)
                                .retrieve()
                                .bodyToMono(Object.class);
                    } else {
                        responseMono = webClient.put()
                                .uri(targetUrl)
                                .retrieve()
                                .bodyToMono(Object.class);
                    }
                    break;
                case "DELETE":
                    responseMono = webClient.delete()
                            .uri(targetUrl)
                            .retrieve()
                            .bodyToMono(Object.class);
                    break;
                case "PATCH":
                    if (body != null) {
                        responseMono = webClient.patch()
                                .uri(targetUrl)
                                .bodyValue(body)
                                .retrieve()
                                .bodyToMono(Object.class);
                    } else {
                        responseMono = webClient.patch()
                                .uri(targetUrl)
                                .retrieve()
                                .bodyToMono(Object.class);
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported HTTP method: " + method);
            }

            // Добавляем обработку ошибок
            responseMono = responseMono.onErrorResume(ex -> {
                log.error("Error proxying request to {}", targetUrl, ex);
                return Mono.empty();
            });

            Object responseBody = responseMono.block();
            
            if (responseBody != null) {
                log.info("Successfully proxied {} request to {}", method, targetUrl);
                return ResponseEntity.ok(responseBody);
            } else {
                log.warn("Empty response from proxied request to {}", targetUrl);
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            log.error("Error proxying {} request to {}", method, targetUrl, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    private String buildTargetUrl(String path, Map<String, String> queryParams) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(proxyBaseUrl + path);
        
        queryParams.forEach(builder::queryParam);
        
        return builder.toUriString();
    }
}
