package com.jellyone.oscars.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Movies & Oscars API")
                        .version("1.0.3")
                        .description("""
                                RESTful API для управления коллекцией фильмов и вспомогательных операций с Оскарами.
                                Второй сервис (Oscars) использует данные первого сервиса (Movies) для своих операций.
                                """))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Локальный сервер")
                ))
                .tags(List.of(
                        new Tag()
                                .name("Oscars")
                                .description("Дополнительные операции с наградами и статистикой Оскаров"),
                        new Tag()
                                .name("Movies Proxy")
                                .description("Прокси для перенаправления запросов к фильмам на внешний сервис")
                ));
    }

    @Bean
    public GroupedOpenApi oscarsApi() {
        return GroupedOpenApi.builder()
                .group("oscars")
                .pathsToMatch("/oscars/**")
                .build();
    }

    @Bean
    public GroupedOpenApi moviesProxyApi() {
        return GroupedOpenApi.builder()
                .group("movies-proxy")
                .pathsToMatch("/api/movies/**")
                .build();
    }
}
