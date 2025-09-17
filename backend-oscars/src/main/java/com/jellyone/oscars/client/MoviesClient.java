package com.jellyone.oscars.client;

import com.jellyone.oscars.model.Movie;
import com.jellyone.oscars.model.MoviePatch;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class MoviesClient {

    private final RestTemplate restTemplate;
    @Value("${movies.api.base-url:http://localhost:8080}")
    private String baseUrl;

    private static final String URL_WITH_ID = "/movies/{id}";

    public Movie getMovieById(long id) {
        try {
            return restTemplate.getForObject(baseUrl + URL_WITH_ID, Movie.class, id);
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            if (e.getStatusCode().value() == 204) {
                return null; // Фильм не найден
            }
            throw e;
        } catch (Exception e) {
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
            ResponseEntity<List<Movie>> response = restTemplate.exchange(
                    builder.toUriString(),
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Movie>>() {}
            );
            return response.getBody() != null ? response.getBody() : List.of();
        } catch (Exception e) {
            return List.of();
        }
    }

    public List<Movie> getAllMovies() {
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
        
        return allMovies;
    }

    public List<Movie> getMoviesByNamePrefix(String prefix) {
        try {
            ResponseEntity<List<Movie>> response = restTemplate.exchange(
                    baseUrl + "/movies/name-starts-with/{prefix}",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Movie>>() {},
                    prefix
            );
            return response.getBody() != null ? response.getBody() : List.of();
        } catch (Exception e) {
            return List.of();
        }
    }

    public Movie patchMovie(long id, MoviePatch patch) {
        try {
            HttpEntity<MoviePatch> request = new HttpEntity<>(patch);
            return restTemplate.patchForObject(baseUrl + URL_WITH_ID, request, Movie.class, id);
        } catch (Exception e) {
            return null;
        }
    }

    public void deleteMovie(long id) {
        try {
            restTemplate.delete(baseUrl + URL_WITH_ID, id);
        } catch (Exception e) {
            // Игнорируем ошибки при удалении
        }
    }

    public void deleteMoviesByOscars(int count) {
        try {
            restTemplate.delete(baseUrl + "/movies/oscarsCount/{count}", count);
        } catch (Exception e) {
            // Игнорируем ошибки при удалении
        }
    }

    @SuppressWarnings("unchecked")
    public Map<String, Integer> countMoviesWithOscarsLessThan(int count) {
        try {
            return restTemplate.getForObject(baseUrl + "/movies/count/oscars-less-than/{count}", Map.class, count);
        } catch (Exception e) {
            return Map.of("count", 0);
        }
    }
}


