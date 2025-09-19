package com.jellyone.oscars.service;

import com.jellyone.oscars.client.MoviesClient;
import com.jellyone.oscars.model.Movie;
import com.jellyone.oscars.model.MoviePatch;
import com.jellyone.oscars.model.Person;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class OscarsService {
    private final MoviesClient moviesClient;
    private final CallbackNotifier callbackNotifier;

    public List<Person> getOscarLosers() {
        log.info("OscarsService: Getting Oscar losers...");
        try {
            // Получаем все фильмы
            List<Movie> allMovies = moviesClient.getAllMovies();
            log.info("OscarsService: Retrieved {} movies from Movies API", allMovies.size());

            // Извлекаем всех сценаристов из фильмов, у которых нет Оскаров (oscarsCount = 0 или null)
            List<Person> losers = allMovies.stream()
                    .filter(movie -> movie.screenwriter() != null &&
                            (movie.oscarsCount() == null || movie.oscarsCount() == 0))
                    .map(Movie::screenwriter)
                    .distinct()
                    .collect(Collectors.toList());

            log.info("OscarsService: Found {} Oscar losers", losers.size());
            return losers;
        } catch (Exception e) {
            log.error("OscarsService: Error getting Oscar losers", e);
            return List.of();
        }
    }

    public Map<String, Object> honorMoviesByLength(double minLength, int oscarsToAdd, String callbackUrl) {
        log.info("OscarsService: Honoring movies by length - minLength: {}, oscarsToAdd: {}", minLength, oscarsToAdd);
        try {
            // Получаем все фильмы
            List<Movie> allMovies = moviesClient.getAllMovies();
            log.info("OscarsService: Retrieved {} movies from Movies API", allMovies.size());

            // Фильтруем по длине (используем x и y координаты для определения длины)
            List<Movie> filteredMovies = allMovies.stream()
                    .filter(movie -> movie.coordinates() != null &&
                            movie.coordinates().x() != null &&
                            movie.coordinates().y() != null &&
                            (movie.coordinates().x() + movie.coordinates().y()) > minLength)
                    .toList();

            log.info("OscarsService: Found {} movies with length > {}", filteredMovies.size(), minLength);

            // Обновляем количество оскаров для каждого фильма
            List<Movie> updatedMovies = new ArrayList<>();
            for (Movie movie : filteredMovies) {
                try {
                    log.info("OscarsService: Updating movie ID {} - {}", movie.id(), movie.name());
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
                        log.info("OscarsService: Successfully updated movie ID {} - new oscars count: {}", movie.id(), updatedMovie.oscarsCount());
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
                                    log.error("OscarsService: Callback error for movie {}", movie.id(), callbackException);
                                }
                            }).start();
                        }
                    } else {
                        log.warn("OscarsService: Failed to update movie ID {}", movie.id());
                    }
                } catch (Exception movieUpdateException) {
                    log.error("OscarsService: Error updating movie ID {}", movie.id(), movieUpdateException);
                }
            }

            log.info("OscarsService: Successfully updated {} movies", updatedMovies.size());
            Map<String, Object> result = new HashMap<>();
            result.put("updatedCount", updatedMovies.size());
            result.put("updatedMovies", updatedMovies);
            return result;
        } catch (Exception e) {
            log.error("OscarsService: Error in honorMoviesByLength", e);
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
                                    log.error("OscarsService: Callback error for movie {}", movie.id(), callbackException);
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
            log.info("OscarsService: Added Oscars {}", oscarsToAdd);
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
                        log.info("OscarsService: callbackUrl: {}", callbackUrl);
                    } catch (Exception callbackException) {
                        log.error("OscarsService: Callback error for movie {}", updatedMovie.id(), callbackException);
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