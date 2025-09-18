package com.jellyone.oscars.service;

import com.jellyone.oscars.client.MoviesClient;
import com.jellyone.oscars.model.Movie;
import com.jellyone.oscars.model.MoviePatch;
import com.jellyone.oscars.model.Person;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class OscarsService {
    private final MoviesClient moviesClient;
    private final CallbackNotifier callbackNotifier;

    public List<Person> getOscarLosers() {
        System.out.println("OscarsService: Getting Oscar losers...");
        try {
            // Получаем все фильмы
            List<Movie> allMovies = moviesClient.getAllMovies();
            System.out.println("OscarsService: Retrieved " + allMovies.size() + " movies from Movies API");
            
            // Извлекаем всех сценаристов из фильмов, у которых нет Оскаров (oscarsCount = 0 или null)
            List<Person> losers = allMovies.stream()
                    .filter(movie -> movie.screenwriter() != null && 
                                   (movie.oscarsCount() == null || movie.oscarsCount() == 0))
                    .map(Movie::screenwriter)
                    .distinct()
                    .collect(Collectors.toList());
            
            System.out.println("OscarsService: Found " + losers.size() + " Oscar losers");
            return losers;
        } catch (Exception e) {
            System.err.println("OscarsService: Error getting Oscar losers: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }

    public Map<String, Object> honorMoviesByLength(double minLength, int oscarsToAdd, String callbackUrl) {
        System.out.println("OscarsService: Honoring movies by length - minLength: " + minLength + ", oscarsToAdd: " + oscarsToAdd);
        try {
            // Получаем все фильмы
            List<Movie> allMovies = moviesClient.getAllMovies();
            System.out.println("OscarsService: Retrieved " + allMovies.size() + " movies from Movies API");
            
            // Фильтруем по длине (используем x и y координаты для определения длины)
            List<Movie> filteredMovies = allMovies.stream()
                    .filter(movie -> movie.coordinates() != null && 
                                   movie.coordinates().x() != null && 
                                   movie.coordinates().y() != null && 
                                   (movie.coordinates().x() + movie.coordinates().y()) > minLength)
                    .toList();
            
            System.out.println("OscarsService: Found " + filteredMovies.size() + " movies with length > " + minLength);

            // Обновляем количество оскаров для каждого фильма
            List<Movie> updatedMovies = new ArrayList<>();
            for (Movie movie : filteredMovies) {
                try {
                    System.out.println("OscarsService: Updating movie ID " + movie.id() + " - " + movie.name());
                    MoviePatch patch = new MoviePatch(
                            movie.name(),
                            movie.coordinates(),
                            (movie.oscarsCount() != null ? movie.oscarsCount() : 0) + oscarsToAdd,
                            movie.goldenPalmCount(),
                            movie.budget(),
                            movie.genre(),
                            movie.screenwriter()
                    );
                    
                    Movie updatedMovie = moviesClient.patchMovie(movie.id(), patch);
                    if (updatedMovie != null) {
                        updatedMovies.add(updatedMovie);
                        System.out.println("OscarsService: Successfully updated movie ID " + movie.id() + " - new oscars count: " + updatedMovie.oscarsCount());
                        // Асинхронный коллбэк с задержкой
                        if (callbackUrl != null && !callbackUrl.isBlank()) {
                            // Запускаем коллбэк в отдельном потоке
                            new Thread(() -> {
                                try {
                                    // Задержка в 3 секунды перед отправкой коллбэка
                                    Thread.sleep(3000);
                                    
                                    Map<String, Object> payload = new HashMap<>();
                                    payload.put("movieId", updatedMovie.id());
                                    payload.put("newOscarsCount", updatedMovie.oscarsCount());
                                    payload.put("updatedMovies", updatedMovies);
                                    callbackNotifier.postJson(callbackUrl, payload);
                                } catch (Exception callbackException) {
                                    System.err.println("OscarsService: Callback error for movie " + movie.id() + ": " + callbackException.getMessage());
                                }
                            }).start();
                        }
                    } else {
                        System.err.println("OscarsService: Failed to update movie ID " + movie.id());
                    }
                } catch (Exception movieUpdateException) {
                    System.err.println("OscarsService: Error updating movie ID " + movie.id() + ": " + movieUpdateException.getMessage());
                    movieUpdateException.printStackTrace();
                }
            }

            System.out.println("OscarsService: Successfully updated " + updatedMovies.size() + " movies");
            Map<String, Object> result = new HashMap<>();
            result.put("updatedCount", updatedMovies.size());
            result.put("updatedMovies", updatedMovies);
            return result;
        } catch (Exception e) {
            System.err.println("OscarsService: Error in honorMoviesByLength: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> result = new HashMap<>();
            result.put("updatedCount", 0);
            result.put("updatedMovies", List.of());
            return result;
        }
    }

    public Map<String, Object> honorMoviesWithFewOscars(int maxOscars, int oscarsToAdd, String callbackUrl) {
        try {
            // Получаем все фильмы
            List<Movie> allMovies = moviesClient.getAllMovies();
            
            // Фильтруем по количеству оскаров
            List<Movie> filteredMovies = allMovies.stream()
                    .filter(movie -> movie.oscarsCount() != null && 
                                   movie.oscarsCount() <= maxOscars)
                    .collect(Collectors.toList());

            // Обновляем количество оскаров для каждого фильма
            List<Movie> updatedMovies = new ArrayList<>();
            for (Movie movie : filteredMovies) {
                try {
                    MoviePatch patch = new MoviePatch(
                            movie.name(),
                            movie.coordinates(),
                            movie.oscarsCount() + oscarsToAdd,
                            movie.goldenPalmCount(),
                            movie.budget(),
                            movie.genre(),
                            movie.screenwriter()
                    );
                    
                    Movie updatedMovie = moviesClient.patchMovie(movie.id(), patch);
                    if (updatedMovie != null) {
                        updatedMovies.add(updatedMovie);
                        // Асинхронный коллбэк с задержкой
                        if (callbackUrl != null && !callbackUrl.isBlank()) {
                            // Запускаем коллбэк в отдельном потоке
                            new Thread(() -> {
                                try {
                                    // Задержка в 3 секунды перед отправкой коллбэка
                                    Thread.sleep(3000);
                                    
                                    Map<String, Object> payload = new HashMap<>();
                                    payload.put("movieId", updatedMovie.id());
                                    payload.put("addedOscars", oscarsToAdd);
                                    payload.put("updatedMovies", updatedMovies);
                                    callbackNotifier.postJson(callbackUrl, payload);
                                } catch (Exception callbackException) {
                                    System.err.println("OscarsService: Callback error for movie " + movie.id() + ": " + callbackException.getMessage());
                                }
                            }).start();
                        }
                    }
                } catch (Exception movieUpdateException) {
                    // Игнорируем ошибки обновления отдельных фильмов
                }
            }

            Map<String, Object> result = new HashMap<>();
            result.put("updatedCount", updatedMovies.size());
            result.put("updatedMovies", updatedMovies);
            return result;
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("updatedCount", 0);
            result.put("updatedMovies", List.of());
            return result;
        }
    }

    public List<Map<String, Object>> getOscarsByMovie(long movieId, int page, int size) {
        try {
            // Проверяем существование фильма
            Movie movie = moviesClient.getMovieById(movieId);
            if (movie == null) {
                return List.of();
            }
            
            // Возвращаем количество оскаров как список чисел
            List<Map<String, Object>> oscars = new ArrayList<>();
            if (movie.oscarsCount() != null && movie.oscarsCount() > 0) {
                for (int i = 1; i <= movie.oscarsCount(); i++) {
                    Map<String, Object> oscar = new HashMap<>();
                    oscar.put("awardId", i);
                    //TODO Просто выпилить это из АПИ
                    oscar.put("date", "2024-01-01"); // Примерная дата
                    oscar.put("category", "Best Picture"); // Примерная категория
                    oscars.add(oscar);
                }
            }
            
            return oscars;
        } catch (Exception e) {
            return List.of();
        }
    }

    public Map<String, Object> addOscars(long movieId, int oscarsToAdd, String callbackUrl) {
        try {
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
                    (movie.oscarsCount() != null ? movie.oscarsCount() : 0) + oscarsToAdd,
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
            // Асинхронный коллбэк с задержкой
            System.out.println("OscarsService: Added Oscars " + oscarsToAdd);
            if (callbackUrl != null && !callbackUrl.isBlank()) {
                // Запускаем коллбэк в отдельном потоке
                new Thread(() -> {
                    try {
                        // Задержка в 3 секунды перед отправкой коллбэка
                        Thread.sleep(3000);
                        
                        Map<String, Object> payload = new HashMap<>();
                        payload.put("movieId", updatedMovie.id());
                        payload.put("category", "UPDATE");
                        payload.put("date", LocalDate.now().toString());
                        payload.put("addedOscars", oscarsToAdd);
                        callbackNotifier.postJson(callbackUrl, payload);
                        System.out.println("OscarsService: callbackUrl: " + callbackUrl);
                    } catch (Exception callbackException) {
                        System.err.println("OscarsService: Callback error for movie " + updatedMovie.id() + ": " + callbackException.getMessage());
                    }
                }).start();
            }
            return result;
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("updatedCount", 0);
            result.put("updatedMovies", List.of());
            return result;
        }
    }

    public boolean deleteOscarsByMovie(long movieId) {
        try {
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
        } catch (Exception e) {
            return false;
        }
    }
}