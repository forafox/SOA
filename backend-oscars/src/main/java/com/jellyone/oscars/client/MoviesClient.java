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
    @Value("${movies.api.base-url:http://localhost:8081}")
    private String baseUrl;

    private static final String URL_WITH_ID = "/movies/{id}";

    public Movie getMovieById(long id) {
        System.out.println("MoviesClient: Getting movie by ID " + id + " from " + baseUrl + URL_WITH_ID);
        try {
            Movie movie = restTemplate.getForObject(baseUrl + URL_WITH_ID, Movie.class, id);
            System.out.println("MoviesClient: Retrieved movie: " + (movie != null ? movie.name() : "null"));
            return movie;
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            System.err.println("MoviesClient: HTTP error getting movie " + id + ": " + e.getStatusCode());
            if (e.getStatusCode().value() == 204) {
                return null; // Фильм не найден
            }
            throw e;
        } catch (Exception e) {
            System.err.println("MoviesClient: Error getting movie " + id + ": " + e.getMessage());
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
        System.out.println("MoviesClient: Getting all movies from " + baseUrl + "/movies");
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
        
        System.out.println("MoviesClient: Retrieved total " + allMovies.size() + " movies");
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
        System.out.println("MoviesClient: Patching movie ID " + id + " with oscars count: " + patch.oscarsCount());
        try {
            HttpEntity<MoviePatch> request = new HttpEntity<>(patch);
            // Используем PUT вместо PATCH, так как Jersey не поддерживает PATCH через RestTemplate
            Movie updatedMovie = restTemplate.exchange(
                baseUrl + URL_WITH_ID, 
                HttpMethod.PUT, 
                request, 
                Movie.class, 
                id
            ).getBody();
            System.out.println("MoviesClient: Successfully patched movie ID " + id + " - new oscars count: " + (updatedMovie != null ? updatedMovie.oscarsCount() : "null"));
            return updatedMovie;
        } catch (Exception e) {
            System.err.println("MoviesClient: Error patching movie " + id + ": " + e.getMessage());
            e.printStackTrace();
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


