package com.blps.repository;

import com.blps.model.Movie;

import java.sql.SQLException;
import java.util.List;

public interface MovieRepository {
    
    Movie create(Movie movie) throws SQLException;
    
    Movie getById(Long id) throws SQLException;
    
    List<Movie> getAll() throws SQLException;
    
    Movie update(Movie movie) throws SQLException;
    
    boolean deleteById(Long id) throws SQLException;
}
