package com.blps.service;

import com.blps.model.Movie;
import com.blps.model.MovieGenre;
import com.blps.repository.MovieRepository;

import java.sql.SQLException;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;

    public Movie createMovie(Movie movie) throws SQLException {
        log.info("Creating movie - {}", movie.getName());
        return movieRepository.create(movie);
    }

    public Movie getMovieById(Long id) throws SQLException {
        log.info("Getting movie by ID - {}", id);
        return movieRepository.getById(id);
    }

    public List<Movie> getMovies(String name, MovieGenre genre, String sort, int page, int size) throws SQLException {
        log.info("Getting movies with filters - name: {}, genre: {}, sort: {}, page: {}, size: {}",
                name, genre, sort, page, size);
        return movieRepository.getAll(name, genre, sort, page, size);
    }

    public Movie updateMovie(Long id, Movie partialMovie) throws SQLException {
        log.info("Updating movie with ID - {}", id);

        Movie existing = movieRepository.getById(id);
        if (existing == null) {
            log.warn("Movie with ID {} not found for update", id);
            return null;
        }

        mergeMovie(existing, partialMovie);

        log.info("Movie with ID {} updated successfully", id);
        return movieRepository.update(existing);
    }

    public boolean deleteMovie(Long id) throws SQLException {
        log.info("Deleting movie with ID - {}", id);
        return movieRepository.deleteById(id);
    }

    public boolean deleteMoviesByOscarsCount(int count) throws SQLException {
        log.info("Deleting movies with oscars count - {}", count);

        List<Movie> moviesToDelete = movieRepository.getAll().stream()
                .filter(m -> m.getOscarsCount() != null && m.getOscarsCount() == count)
                .toList();

        moviesToDelete.forEach(m -> {
            try {
                movieRepository.deleteById(m.getId());
            } catch (SQLException e) {
                log.error("Failed to delete movie with ID {}", m.getId(), e);
            }
        });

        boolean anyDeleted = !moviesToDelete.isEmpty();
        log.info("Deleted movies with oscars count {}: {}", count, anyDeleted);
        return anyDeleted;
    }


    public long countMoviesWithOscarsLessThan(int count) throws SQLException {
        log.info("Counting movies with oscars less than - {}", count);
        long result = movieRepository.getAll().stream()
                .filter(m -> m.getOscarsCount() != null && m.getOscarsCount() < count)
                .count();
        log.info("Found {} movies with oscars less than {}", result, count);
        return result;
    }

    public List<Movie> getMoviesByNamePrefix(String prefix) throws SQLException {
        log.info("Getting movies with name prefix - {}", prefix);
        List<Movie> result = movieRepository.getAll().stream()
                .filter(m -> m.getName() != null && m.getName().startsWith(prefix))
                .toList();
        log.info("Found {} movies with prefix {}", result.size(), prefix);
        return result;
    }

    private void mergeMovie(Movie existing, Movie partial) {
        if (partial.getName() != null) existing.setName(partial.getName());
        if (partial.getCoordinates() != null) existing.setCoordinates(partial.getCoordinates());
        if (partial.getOscarsCount() != null) existing.setOscarsCount(partial.getOscarsCount());
        if (partial.getGoldenPalmCount() != null) existing.setGoldenPalmCount(partial.getGoldenPalmCount());
        if (partial.getBudget() != null) existing.setBudget(partial.getBudget());
        if (partial.getGenre() != null) existing.setGenre(partial.getGenre());
        if (partial.getScreenwriter() != null) existing.setScreenwriter(partial.getScreenwriter());
    }
}