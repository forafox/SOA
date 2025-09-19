package com.blps.repository;

import com.blps.model.Movie;
import com.blps.model.MovieGenre;

import java.sql.SQLException;
import java.util.List;

public interface MovieRepository {
    
    Movie create(Movie movie) throws SQLException;
    
    Movie getById(Long id) throws SQLException;

    Movie update(Movie movie) throws SQLException;

    List<Movie> getAll() throws SQLException;
    
    boolean deleteById(Long id) throws SQLException;

    List<Movie> getAll(String name, MovieGenre genre, String sort, int page, int size) throws SQLException;
}
