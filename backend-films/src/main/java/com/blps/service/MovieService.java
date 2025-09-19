package com.blps.service;

import com.blps.model.Movie;
import com.blps.model.MovieGenre;
import com.blps.repository.MovieRepository;
import lombok.extern.slf4j.Slf4j;

import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class MovieService {

    private final MovieRepository movieRepository;

    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public Movie createMovie(Movie movie) throws SQLException {
        log.info("Service: Creating movie - {}", movie.getName());
        return movieRepository.create(movie);
    }

    public Movie getMovieById(Long id) throws SQLException {
        log.info("Service: Getting movie by ID - {}", id);
        return movieRepository.getById(id);
    }

    public List<Movie> getMovies(String name, MovieGenre genre, String sort, int page, int size) throws SQLException {
        log.info("Service: Getting movies with filters - name: {}, genre: {}, sort: {}, page: {}, size: {}", name, genre, sort, page, size);
        List<Movie> result = movieRepository.getAll();

        // Filter by name
        if (name != null && !name.isEmpty()) {
            result = result.stream()
                    .filter(m -> m.getName() != null && m.getName().contains(name))
                    .collect(Collectors.toList());
        }

        // Filter by genre
        if (genre != null) {
            result = result.stream()
                    .filter(m -> genre.equals(m.getGenre()))
                    .collect(Collectors.toList());
        }

        // Sort
        if (sort != null) {
            boolean desc = sort.contains(":desc");
            String field = sort.replace(":desc", "");

            Comparator<Movie> comparator = getComparator(field);
            if (desc) {
                comparator = comparator.reversed();
            }
            result = result.stream().sorted(comparator).collect(Collectors.toList());
        }

        // Pagination
        int fromIndex = (page - 1) * size;
        if (fromIndex >= result.size()) {
            log.info("Service: No movies found for pagination");
            return List.of();
        }
        int toIndex = Math.min(fromIndex + size, result.size());
        List<Movie> paginatedResult = result.subList(fromIndex, toIndex);
        log.info("Service: Returning {} movies", paginatedResult.size());
        return paginatedResult;
    }

    public Movie updateMovie(Long id, Movie partialMovie) throws SQLException {
        log.info("Service: Updating movie with ID - {}", id);
        Movie existing = movieRepository.getById(id);
        if (existing == null) {
            log.info("Service: Movie not found for update");
            return null;
        }

        // Update fields
        if (partialMovie.getName() != null) {
            existing.setName(partialMovie.getName());
        }
        if (partialMovie.getCoordinates() != null) {
            existing.setCoordinates(partialMovie.getCoordinates());
        }
        if (partialMovie.getOscarsCount() != null) {
            existing.setOscarsCount(partialMovie.getOscarsCount());
        }
        if (partialMovie.getGoldenPalmCount() != null) {
            existing.setGoldenPalmCount(partialMovie.getGoldenPalmCount());
        }
        if (partialMovie.getBudget() != null) {
            existing.setBudget(partialMovie.getBudget());
        }
        if (partialMovie.getGenre() != null) {
            existing.setGenre(partialMovie.getGenre());
        }
        if (partialMovie.getScreenwriter() != null) {
            existing.setScreenwriter(partialMovie.getScreenwriter());
        }

        log.info("Service: Movie updated successfully");
        return movieRepository.update(existing);
    }

    public boolean deleteMovie(Long id) throws SQLException {
        log.info("Service: Deleting movie with ID - {}", id);
        return movieRepository.deleteById(id);
    }

    public boolean deleteMoviesByOscarsCount(int count) throws SQLException {
        log.info("Service: Deleting movies with oscars count - {}", count);
        List<Movie> all = movieRepository.getAll();
        boolean anyDeleted = false;
        for (Movie m : all) {
            if (m.getOscarsCount() != null && m.getOscarsCount() == count) {
                movieRepository.deleteById(m.getId());
                anyDeleted = true;
            }
        }
        log.info("Service: Deleted movies with oscars count {}: {}", count, anyDeleted);
        return anyDeleted;
    }

    public long countMoviesWithOscarsLessThan(int count) throws SQLException {
        log.info("Service: Counting movies with oscars less than - {}", count);
        long result = movieRepository.getAll().stream()
                .filter(m -> m.getOscarsCount() != null && m.getOscarsCount() < count)
                .count();
        log.info("Service: Found {} movies with oscars less than {}", result, count);
        return result;
    }

    public List<Movie> getMoviesByNamePrefix(String prefix) throws SQLException {
        log.info("Service: Getting movies with name prefix - {}", prefix);
        List<Movie> result = movieRepository.getAll().stream()
                .filter(m -> m.getName() != null && m.getName().startsWith(prefix))
                .collect(Collectors.toList());
        log.info("Service: Found {} movies with prefix {}", result.size(), prefix);
        return result;
    }

    private Comparator<Movie> getComparator(String field) {
        return switch (field) {
            case "oscarsCount" -> Comparator.comparing(Movie::getOscarsCount, Comparator.nullsLast(Long::compareTo));
            case "id" -> Comparator.comparing(Movie::getId, Comparator.nullsLast(Long::compareTo));
            case "name" -> Comparator.comparing(Movie::getName, Comparator.nullsLast(String::compareTo));
            default -> Comparator.comparing(Movie::getId, Comparator.nullsLast(Long::compareTo));
        };
    }
}
