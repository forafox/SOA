package com.jellyone.oscars.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
@Slf4j
public class WebClientConfig {
    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        HttpClient httpClient = HttpClient.create()
                .keepAlive(false)
                .compress(true)
                .responseTimeout(Duration.ofSeconds(10))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5_000)
                .doOnConnected(connection ->
                        connection
                                .addHandlerLast(new ReadTimeoutHandler(10, TimeUnit.SECONDS))
                                .addHandlerLast(new WriteTimeoutHandler(10, TimeUnit.SECONDS))
                );

        return builder
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .defaultHeader("Connection", "close")
                .filter(logRequest())
                .filter(logResponse())
                .build();
    }

    private ExchangeFilterFunction logRequest() {
        return (request, next) -> {
            log.info("WebClient Request: {} {}", request.method(), request.url());
            return next.exchange(request);
        };
    }

    private ExchangeFilterFunction logResponse() {
        return (request, next) -> next.exchange(request)
                .doOnNext(response -> log.info("WebClient Response: {} {} -> {}", request.method(), request.url(), response.statusCode()))
                .onErrorResume(ex -> {
                    log.error("WebClient Error: {} {}", request.method(), request.url(), ex);
                    return Mono.error(ex);
                });
    }
}


