package com.jellyone.oscars.service;

import com.jellyone.oscars.client.MoviesClient;
import com.jellyone.oscars.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OscarsService {
    private final MoviesClient moviesClient;
    private final CallbackNotifier callbackNotifier;

    public List<Person> getOscarLosers() {
        try {
            return moviesClient.getAllMovies().stream()
                    .filter(movie -> movie.screenwriter() != null &&
                            (movie.oscarsCount() == null || movie.oscarsCount() == 0))
                    .map(Movie::screenwriter)
                    .distinct()
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error getting Oscar losers", e);
            return List.of();
        }
    }

    public MovieUpdateResponse honorMoviesByLength(double minLength, int oscarsToAdd, String callbackUrl) {
        try {
            List<Movie> filtered = moviesClient.getAllMovies().stream()
                    .filter(movie -> movie.coordinates() != null &&
                            movie.coordinates().x() != null &&
                            movie.coordinates().y() != null &&
                            (movie.coordinates().x() + movie.coordinates().y()) > minLength)
                    .toList();

            return updateMoviesOscars(filtered, oscarsToAdd, callbackUrl);
        } catch (Exception e) {
            log.error("Error honoring movies by length", e);
            return new MovieUpdateResponse(0, List.of());
        }
    }

    public MovieUpdateResponse honorMoviesWithFewOscars(int maxOscars, int oscarsToAdd, String callbackUrl) {
        try {
            List<Movie> filtered = moviesClient.getAllMovies().stream()
                    .filter(movie -> movie.oscarsCount() != null && movie.oscarsCount() <= maxOscars)
                    .toList();

            return updateMoviesOscars(filtered, oscarsToAdd, callbackUrl);
        } catch (Exception e) {
            log.error("Error honoring movies with few Oscars", e);
            return new MovieUpdateResponse(0, List.of());
        }
    }

    public List<Award> getOscarsByMovie(long movieId, int page, int size) {
        try {
            Movie movie = moviesClient.getMovieById(movieId);
            if (movie == null || movie.oscarsCount() == null || movie.oscarsCount() <= 0) {
                return List.of();
            }

            List<Award> awards = new ArrayList<>();
            for (int i = 1; i <= movie.oscarsCount(); i++) {
                awards.add(new Award(i, "2024-01-01", "Best Picture"));
            }
            return awards;
        } catch (Exception e) {
            log.error("Error getting Oscars by movie", e);
            return List.of();
        }
    }

    public MovieUpdateResponse addOscars(long movieId, int oscarsToAdd, String callbackUrl) {
        try {
            Movie movie = moviesClient.getMovieById(movieId);
            if (movie == null) return new MovieUpdateResponse(0, List.of());

            MoviePatch patch = buildPatch(movie, oscarsToAdd);
            Movie updated = moviesClient.patchMovie(movieId, patch);

            if (updated != null) {
                sendCallback(callbackUrl, Map.of(
                        "movieId", updated.id(),
                        "category", "UPDATE",
                        "date", LocalDate.now().toString(),
                        "addedOscars", oscarsToAdd
                ));
                return new MovieUpdateResponse(1, List.of(updated));
            }
            return new MovieUpdateResponse(0, List.of());
        } catch (Exception e) {
            log.error("Error adding Oscars", e);
            return new MovieUpdateResponse(0, List.of());
        }
    }

    public boolean deleteOscarsByMovie(long movieId) {
        try {
            Movie movie = moviesClient.getMovieById(movieId);
            if (movie == null || movie.oscarsCount() == null || movie.oscarsCount() == 0) return false;

            MoviePatch patch = buildPatch(movie, -movie.oscarsCount());
            return moviesClient.patchMovie(movieId, patch) != null;
        } catch (Exception e) {
            log.error("Error deleting Oscars", e);
            return false;
        }
    }

    private MoviePatch buildPatch(Movie movie, int oscarsToAdd) {
        int newCount = (movie.oscarsCount() != null ? movie.oscarsCount() : 0) + oscarsToAdd;
        return new MoviePatch(
                movie.name(),
                movie.coordinates(),
                newCount,
                movie.goldenPalmCount(),
                movie.budget(),
                movie.genre(),
                movie.screenwriter()
        );
    }

    private MovieUpdateResponse updateMoviesOscars(List<Movie> movies, int oscarsToAdd, String callbackUrl) {
        List<Movie> updatedMovies = new ArrayList<>();
        for (Movie movie : movies) {
            try {
                MoviePatch patch = buildPatch(movie, oscarsToAdd);
                Movie updated = moviesClient.patchMovie(movie.id(), patch);
                if (updated != null) {
                    updatedMovies.add(updated);
                    if (callbackUrl != null && !callbackUrl.isBlank()) {
                        Map<String, Object> payload = new HashMap<>();
                        payload.put("movieId", updated.id());
                        payload.put("addedOscars", oscarsToAdd);
                        payload.put("updatedMovies", updatedMovies);
                        sendCallback(callbackUrl, payload);
                    }
                }
            } catch (Exception e) {
                log.error("Error updating movie ID {}", movie.id(), e);
            }
        }
        return new MovieUpdateResponse(updatedMovies.size(), updatedMovies);
    }

    private void sendCallback(String callbackUrl, Map<String, Object> payload) {
        if (callbackUrl == null || callbackUrl.isBlank()) return;
        new Thread(() -> {
            try {
                Thread.sleep(3000);
                callbackNotifier.postJson(callbackUrl, payload);
            } catch (Exception e) {
                log.error("Callback error", e);
            }
        }).start();
    }
}