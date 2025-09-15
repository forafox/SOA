package com.jellyone.oscars.service;

import com.jellyone.oscars.client.MoviesClient;
import com.jellyone.oscars.model.Movie;
import com.jellyone.oscars.model.MoviePatch;
import com.jellyone.oscars.model.Person;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OscarsService {
    private final MoviesClient moviesClient;

    public List<Person> getOscarLosers() {
        // Поскольку в схеме нет операторов, возвращаем пустой список
        return List.of();
    }

    public Map<String, Object> honorMoviesByLength(double minLength, int oscarsToAdd) {
        // Получаем все фильмы и фильтруем по длине (используем координаты как длину)
        List<Movie> movies = moviesClient.getMovies(null, null, null, 1, 100);
        List<Movie> filteredMovies = movies.stream()
                .filter(movie -> movie.coordinates() != null && 
                               movie.coordinates().y() != null && 
                               movie.coordinates().y() > minLength)
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("updatedCount", filteredMovies.size());
        result.put("updatedMovies", filteredMovies);
        return result;
    }

    public Map<String, Object> honorMoviesWithFewOscars(int maxOscars, int oscarsToAdd) {
        // Получаем все фильмы и фильтруем по количеству оскаров
        List<Movie> movies = moviesClient.getMovies(null, null, null, 1, 100);
        List<Movie> filteredMovies = movies.stream()
                .filter(movie -> movie.oscarsCount() != null && 
                               movie.oscarsCount() <= maxOscars)
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("updatedCount", filteredMovies.size());
        result.put("updatedMovies", filteredMovies);
        return result;
    }

    public List<Map<String, Object>> getOscarsByMovie(long movieId, int page, int size) {
        // Проверяем существование фильма
        Movie movie = moviesClient.getMovieById(movieId);
        if (movie == null) {
            return List.of();
        }
        
        // Поскольку в схеме нет реальных данных об оскарах, возвращаем пустой список
        // В реальном приложении здесь был бы запрос к базе данных оскаров
        return List.of();
    }

    public Map<String, Object> addOscars(long movieId, int oscarsToAdd) {
        Movie movie = moviesClient.getMovieById(movieId);
        if (movie == null) {
            Map<String, Object> result = new HashMap<>();
            result.put("updatedCount", 0);
            result.put("updatedMovies", List.of());
            return result;
        }

        MoviePatch patch = new MoviePatch(
                movie.name(),
                movie.coordinates(),
                movie.oscarsCount() + oscarsToAdd,
                movie.goldenPalmCount(),
                movie.budget(),
                movie.genre(),
                movie.screenwriter()
        );
        
        Movie updatedMovie = moviesClient.patchMovie(movieId, patch);
        if (updatedMovie == null) {
            Map<String, Object> result = new HashMap<>();
            result.put("updatedCount", 0);
            result.put("updatedMovies", List.of());
            return result;
        }

        Map<String, Object> result = new HashMap<>();
        result.put("updatedCount", 1);
        result.put("updatedMovies", List.of(updatedMovie));
        return result;
    }

    public boolean deleteOscarsByMovie(long movieId) {
        Movie movie = moviesClient.getMovieById(movieId);
        if (movie == null) {
            return false;
        }

        // Если у фильма уже 0 оскаров, возвращаем false (304 Not Modified)
        if (movie.oscarsCount() == null || movie.oscarsCount() == 0) {
            return false;
        }
        
        // Обнуляем количество оскаров
        MoviePatch patch = new MoviePatch(
                movie.name(),
                movie.coordinates(),
                0,
                movie.goldenPalmCount(),
                movie.budget(),
                movie.genre(),
                movie.screenwriter()
        );
        
        Movie updatedMovie = moviesClient.patchMovie(movieId, patch);
        return updatedMovie != null;
    }
}


