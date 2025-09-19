package com.blps.service;

import com.blps.model.Movie;
import com.blps.model.MovieGenre;
import com.blps.repository.MovieRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class MovieService {

    private final MovieRepository movieRepository;

    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public Movie createMovie(Movie movie) throws SQLException {
        System.out.println("Service: Creating movie - " + movie.getName());
        return movieRepository.create(movie);
    }

    public Movie getMovieById(Long id) throws SQLException {
        System.out.println("Service: Getting movie by ID - " + id);
        return movieRepository.getById(id);
    }

    public List<Movie> getMovies(String name, MovieGenre genre, String sort, int page, int size) throws SQLException {
        System.out.println("Service: Getting movies with filters - name: " + name + ", genre: " + genre + ", sort: " + sort + ", page: " + page + ", size: " + size);

        return movieRepository.getAll(name, genre, sort, page, size);
    }

    public Movie updateMovie(Long id, Movie partialMovie) throws SQLException {
        System.out.println("Service: Updating movie with ID - " + id);
        Movie existing = movieRepository.getById(id);
        if (existing == null) {
            System.out.println("Service: Movie not found for update");
            return null;
        }

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

        System.out.println("Service: Movie updated successfully");
        return movieRepository.update(existing);
    }

    public boolean deleteMovie(Long id) throws SQLException {
        System.out.println("Service: Deleting movie with ID - " + id);
        return movieRepository.deleteById(id);
    }

    public boolean deleteMoviesByOscarsCount(int count) throws SQLException {
        System.out.println("Service: Deleting movies with oscars count - " + count);
        List<Movie> all = movieRepository.getAll();
        boolean anyDeleted = false;
        for (Movie m : all) {
            if (m.getOscarsCount() != null && m.getOscarsCount() == count) {
                movieRepository.deleteById(m.getId());
                anyDeleted = true;
            }
        }
        System.out.println("Service: Deleted movies with oscars count " + count + ": " + anyDeleted);
        return anyDeleted;
    }

    public long countMoviesWithOscarsLessThan(int count) throws SQLException {
        System.out.println("Service: Counting movies with oscars less than - " + count);
        long result = movieRepository.getAll().stream()
                .filter(m -> m.getOscarsCount() != null && m.getOscarsCount() < count)
                .count();
        System.out.println("Service: Found " + result + " movies with oscars less than " + count);
        return result;
    }

    public List<Movie> getMoviesByNamePrefix(String prefix) throws SQLException {
        System.out.println("Service: Getting movies with name prefix - " + prefix);
        List<Movie> result = movieRepository.getAll().stream()
                .filter(m -> m.getName() != null && m.getName().startsWith(prefix))
                .collect(Collectors.toList());
        System.out.println("Service: Found " + result.size() + " movies with prefix " + prefix);
        return result;
    }

}
