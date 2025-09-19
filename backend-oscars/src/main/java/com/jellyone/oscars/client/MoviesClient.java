package com.jellyone.oscars.client;

import com.jellyone.oscars.model.Movie;
import com.jellyone.oscars.model.MoviePatch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class MoviesClient {

    private final WebClient webClient;
    @Value("${movies.api.base-url:http://localhost:8081}")
    private String baseUrl;

    private static final String URL_WITH_ID = "/movies/{id}";

    public Movie getMovieById(long id) {
        log.info("MoviesClient: Getting movie by ID {} from {}{}", id, baseUrl, URL_WITH_ID);
        try {
            return webClient.get()
                    .uri(baseUrl + URL_WITH_ID, id)
                    .retrieve()
                    .bodyToMono(Movie.class)
                    .onErrorResume(ex -> {
                        log.error("MoviesClient: Error getting movie {}", id, ex);
                        return reactor.core.publisher.Mono.empty();
                    })
                    .block();
        } catch (Exception e) {
            log.error("MoviesClient: Error getting movie {}", id, e);
            return null;
        }
    }

    public List<Movie> getMovies(String name, String genre, String sort, int page, int size) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(baseUrl + "/movies")
                .queryParam("page", page)
                .queryParam("size", size);
        
        if (name != null) builder.queryParam("name", name);
        if (genre != null) builder.queryParam("genre", genre);
        if (sort != null) builder.queryParam("sort", sort);
        
        try {
            return webClient.get()
                    .uri(builder.toUriString())
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<Movie>>() {})
                    .onErrorResume(ex -> {
                        log.error("MoviesClient: Error getting movies list", ex);
                        return reactor.core.publisher.Mono.just(List.of());
                    })
                    .blockOptional()
                    .orElse(List.of());
        } catch (Exception e) {
            log.error("MoviesClient: Error getting movies list", e);
            return List.of();
        }
    }

    public List<Movie> getAllMovies() {
        log.info("MoviesClient: Getting all movies from {}/movies", baseUrl);
        List<Movie> allMovies = new ArrayList<>();
        int page = 1;
        int size = 100;
        
        while (true) {
            List<Movie> movies = getMovies(null, null, null, page, size);
            if (movies.isEmpty()) {
                break;
            }
            allMovies.addAll(movies);
            page++;
        }
        
        log.info("MoviesClient: Retrieved total {} movies", allMovies.size());
        return allMovies;
    }
    
    public Movie patchMovie(long id, MoviePatch patch) {
        log.info("MoviesClient: Patching movie ID {} with oscars count: {}", id, patch.oscarsCount());
        try {
            Movie updatedMovie = webClient.put()
                    .uri(baseUrl + URL_WITH_ID, id)
                    .bodyValue(patch)
                    .retrieve()
                    .bodyToMono(Movie.class)
                    .onErrorResume(ex -> {
                        log.error("MoviesClient: Error patching movie {}", id, ex);
                        return reactor.core.publisher.Mono.empty();
                    })
                    .block();
            log.info("MoviesClient: Successfully patched movie ID {} - new oscars count: {}", id, (updatedMovie != null ? updatedMovie.oscarsCount() : null));
            return updatedMovie;
        } catch (Exception e) {
            log.error("MoviesClient: Error patching movie {}", id, e);
            return null;
        }
    }
}