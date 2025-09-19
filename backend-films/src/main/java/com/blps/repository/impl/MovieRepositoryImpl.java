package com.blps.repository.impl;

import com.blps.config.DatabaseConfiguration;
import com.blps.model.Coordinates;
import com.blps.model.Movie;
import com.blps.model.MovieGenre;
import com.blps.model.Person;
import com.blps.repository.MovieRepository;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class MovieRepositoryImpl implements MovieRepository {

    @Override
    public Movie create(Movie movie) throws SQLException {
        log.info("Creating movie: {}", movie.getName());
        try (Connection conn = DatabaseConfiguration.getConnection()) {
            conn.setAutoCommit(false);

            Long coordId = null;
            if (movie.getCoordinates() != null) {
                String sqlCoord = "INSERT INTO coordinates (x, y) VALUES (?, ?) RETURNING id";
                try (PreparedStatement stmt = conn.prepareStatement(sqlCoord)) {
                    stmt.setLong(1, movie.getCoordinates().x());
                    stmt.setDouble(2, movie.getCoordinates().y());
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        coordId = rs.getLong("id");
                    }
                }
            }

            Long personId = null;
            if (movie.getScreenwriter() != null) {
                Person p = movie.getScreenwriter();
                String sqlPerson = "INSERT INTO person (name, birthday, height, weight, passport_id) VALUES (?, ?, ?, ?, ?) RETURNING id";
                try (PreparedStatement stmt = conn.prepareStatement(sqlPerson)) {
                    stmt.setString(1, p.name());
                    stmt.setDate(2, p.birthday() != null ? java.sql.Date.valueOf(p.birthday()) : null);
                    stmt.setDouble(3, p.height());
                    stmt.setLong(4, p.weight());
                    stmt.setString(5, p.passportID());
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        personId = rs.getLong("id");
                    }
                }
            }

            String sqlMovie = "INSERT INTO movies (name, creation_date, oscars_count, golden_palm_count, budget, genre, coordinates_id, screenwriter_id) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";
            try (PreparedStatement stmt = conn.prepareStatement(sqlMovie)) {
                stmt.setString(1, movie.getName());
                stmt.setDate(2, java.sql.Date.valueOf(movie.getCreationDate()));
                stmt.setObject(3, movie.getOscarsCount());
                stmt.setObject(4, movie.getGoldenPalmCount());
                stmt.setObject(5, movie.getBudget());
                stmt.setString(6, movie.getGenre().name());
                stmt.setObject(7, coordId);
                stmt.setObject(8, personId);

                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    movie.setId(rs.getLong("id"));
                }
            }

            conn.commit();
            log.info("Movie created successfully with ID: {}", movie.getId());
        }

        return movie;
    }

    @Override
    public Movie getById(Long id) throws SQLException {
        log.info("Getting movie by ID: {}", id);
        String sql = "SELECT m.*, c.x AS coord_x, c.y AS coord_y, " +
                "p.name AS p_name, p.birthday AS p_birthday, p.height AS p_height, p.weight AS p_weight, p.passport_id AS p_passport " +
                "FROM movies m " +
                "LEFT JOIN coordinates c ON m.coordinates_id = c.id " +
                "LEFT JOIN person p ON m.screenwriter_id = p.id " +
                "WHERE m.id = ?";

        try (Connection conn = DatabaseConfiguration.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                log.info("Movie found: {}", rs.getString("name"));
                return mapResultSetToMovie(rs);
            } else {
                log.info("Movie not found with ID: {}", id);
                return null;
            }
        }
    }

    @Override
    public boolean deleteById(Long id) throws SQLException {
        log.info("Deleting movie with ID: {}", id);
        String sql = "DELETE FROM movies WHERE id = ?";
        try (Connection conn = DatabaseConfiguration.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            boolean deleted = stmt.executeUpdate() > 0;
            log.info("Movie deletion result: {}", deleted);
            return deleted;
        }
    }

    @Override
    public Movie update(Movie movie) throws SQLException {
        log.info("Updating movie with ID: {}", movie.getId());
        try (Connection conn = DatabaseConfiguration.getConnection()) {
            conn.setAutoCommit(false);

            // Update Coordinates
            if (movie.getCoordinates() != null) {
                String sqlCoord = "UPDATE coordinates SET x = ?, y = ? WHERE id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sqlCoord)) {
                    stmt.setLong(1, movie.getCoordinates().x());
                    stmt.setDouble(2, movie.getCoordinates().y());
                    stmt.setObject(3, movie.getCoordinates().id());
                    stmt.executeUpdate();
                }
            }

            // Update Person
            if (movie.getScreenwriter() != null) {
                Person p = movie.getScreenwriter();
                String sqlPerson = "UPDATE person SET name = ?, birthday = ?, height = ?, weight = ?, passport_id = ? WHERE id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sqlPerson)) {
                    stmt.setString(1, p.name());
                    stmt.setDate(2, p.birthday() != null ? java.sql.Date.valueOf(p.birthday()) : null);
                    stmt.setDouble(3, p.height());
                    stmt.setLong(4, p.weight());
                    stmt.setString(5, p.passportID());
                    stmt.setObject(6, p.id());
                    stmt.executeUpdate();
                }
            }

            // Update Movie
            String sqlMovie = "UPDATE movies SET name = ?, oscars_count = ?, golden_palm_count = ?, budget = ?, genre = ? WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sqlMovie)) {
                stmt.setString(1, movie.getName());
                stmt.setObject(2, movie.getOscarsCount());
                stmt.setObject(3, movie.getGoldenPalmCount());
                stmt.setObject(4, movie.getBudget());
                stmt.setString(5, movie.getGenre().name());
                stmt.setLong(6, movie.getId());
                stmt.executeUpdate();
            }

            conn.commit();
            log.info("Movie updated successfully");
        }
        return movie;
    }

    @Override
    public List<Movie> getAll() throws SQLException {
        log.info("Getting all movies");
        List<Movie> list = new ArrayList<>();
        String sql = "SELECT m.*, c.x AS coord_x, c.y AS coord_y, " +
                "p.name AS p_name, p.birthday AS p_birthday, p.height AS p_height, p.weight AS p_weight, p.passport_id AS p_passport " +
                "FROM movies m " +
                "LEFT JOIN coordinates c ON m.coordinates_id = c.id " +
                "LEFT JOIN person p ON m.screenwriter_id = p.id";

        try (Connection conn = DatabaseConfiguration.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToMovie(rs));
            }
        }
        log.info("Retrieved {} movies", list.size());
        return list;
    }

    private Movie mapResultSetToMovie(ResultSet rs) throws SQLException {
        Movie movie = new Movie();
        movie.setId(rs.getLong("id"));
        movie.setName(rs.getString("name"));
        movie.setCreationDate(rs.getDate("creation_date").toLocalDate());
        movie.setOscarsCount(rs.getObject("oscars_count", Long.class));
        movie.setGoldenPalmCount(rs.getObject("golden_palm_count", Long.class));
        movie.setBudget(rs.getObject("budget", Float.class));
        movie.setGenre(MovieGenre.valueOf(rs.getString("genre")));

        // Coordinates
        Long coordId = rs.getObject("coordinates_id", Long.class);
        Long x = rs.getObject("coord_x", Long.class);
        Double y = rs.getObject("coord_y", Double.class);
        if (x != null && y != null) {
            Coordinates coord = new Coordinates(coordId, x, y);
            movie.setCoordinates(coord);
        }

        // Screenwriter
        Long personId = rs.getObject("screenwriter_id", Long.class);
        String name = rs.getString("p_name");
        if (name != null) {
            Person p = new Person(
                    personId,
                    name,
                    rs.getDate("p_birthday") != null ? rs.getDate("p_birthday").toLocalDate() : null,
                    rs.getDouble("p_height"),
                    rs.getLong("p_weight"),
                    rs.getString("p_passport")
            );
            movie.setScreenwriter(p);
        }

        return movie;
    }
}
