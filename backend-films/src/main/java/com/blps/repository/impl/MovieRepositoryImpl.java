package com.blps.repository.impl;

import com.blps.config.DatabaseConfiguration;
import com.blps.model.Coordinates;
import com.blps.model.Movie;
import com.blps.model.MovieGenre;
import com.blps.model.Person;
import com.blps.repository.MovieRepository;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class MovieRepositoryImpl implements MovieRepository {

    @Override
    public Movie create(Movie movie) throws SQLException {
        log.info("Creating movie: {}", movie.getName());
        try (Connection conn = DatabaseConfiguration.getConnection()) {
            conn.setAutoCommit(false);

            Long coordId = insertCoordinates(conn, movie.getCoordinates());
            Long personId = insertPerson(conn, movie.getScreenwriter());

            String sql = """
                    INSERT INTO movies
                    (name, creation_date, oscars_count, golden_palm_count, budget, genre, coordinates_id, screenwriter_id)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?) RETURNING id
                    """;

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, movie.getName());
                stmt.setDate(2, Date.valueOf(movie.getCreationDate()));
                stmt.setObject(3, movie.getOscarsCount());
                stmt.setObject(4, movie.getGoldenPalmCount());
                stmt.setObject(5, movie.getBudget());
                stmt.setString(6, movie.getGenre().name());
                stmt.setObject(7, coordId);
                stmt.setObject(8, personId);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        movie.setId(rs.getLong("id"));
                    }
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
        String sql = """
                SELECT m.*, c.x AS coord_x, c.y AS coord_y,
                       p.name AS p_name, p.birthday AS p_birthday, p.height AS p_height, p.weight AS p_weight, p.passport_id AS p_passport
                FROM movies m
                LEFT JOIN coordinates c ON m.coordinates_id = c.id
                LEFT JOIN person p ON m.screenwriter_id = p.id
                WHERE m.id = ?
                """;

        try (Connection conn = DatabaseConfiguration.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    log.info("Movie found: {}", rs.getString("name"));
                    return mapResultSetToMovie(rs);
                }
            }
        }

        log.info("Movie not found with ID: {}", id);
        return null;
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
        try (Connection conn = DatabaseConfiguration.getConnection()) {
            conn.setAutoCommit(false);
            updateCoordinates(conn, movie.getCoordinates());
            updatePerson(conn, movie.getScreenwriter());
            updateMovieRecord(conn, movie);
            conn.commit();
            log.info("Movie updated successfully");
        }
        return movie;
    }

    private void updateMovieRecord(Connection conn, Movie movie) throws SQLException {
        String sql = """
                UPDATE movies
                SET name = ?, oscars_count = ?, golden_palm_count = ?, budget = ?, genre = ?
                WHERE id = ?
                """;
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, movie.getName());
            stmt.setObject(2, movie.getOscarsCount());
            stmt.setObject(3, movie.getGoldenPalmCount());
            stmt.setObject(4, movie.getBudget());
            stmt.setString(5, movie.getGenre().name());
            stmt.setLong(6, movie.getId());
            stmt.executeUpdate();
        }
    }

    @Override
    public List<Movie> getAll() throws SQLException {
        return getAll(null, null, null, 0, 0);
    }

    @Override
    public List<Movie> getAll(String name, MovieGenre genre, String sort, int page, int size) throws SQLException {
        log.info("Getting movies with filters - name: {}, genre: {}, sort: {}, page: {}, size: {}", name, genre, sort, page, size);
        List<Movie> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder("""
                SELECT m.*, c.x AS coord_x, c.y AS coord_y,
                       p.name AS p_name, p.birthday AS p_birthday, p.height AS p_height, p.weight AS p_weight, p.passport_id AS p_passport
                FROM movies m
                LEFT JOIN coordinates c ON m.coordinates_id = c.id
                LEFT JOIN person p ON m.screenwriter_id = p.id
                WHERE 1=1
                """);

        List<Object> params = new ArrayList<>();
        appendNameFilter(sql, params, name);
        appendGenreFilter(sql, params, genre);
        appendSort(sql, sort);

        if (size > 0 && page > 0) {
            sql.append(" LIMIT ? OFFSET ? ");
            params.add(size);
            params.add((page - 1) * size);
        }

        try (Connection conn = DatabaseConfiguration.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToMovie(rs));
                }
            }
        }

        log.info("Retrieved {} movies", list.size());
        return list;
    }

    private void appendNameFilter(StringBuilder sql, List<Object> params, String name) {
        if (name != null && !name.isBlank()) {
            sql.append(" AND m.name ILIKE ? ");
            params.add("%" + name + "%");
        }
    }

    private void appendGenreFilter(StringBuilder sql, List<Object> params, MovieGenre genre) {
        if (genre != null) {
            sql.append(" AND m.genre = ? ");
            params.add(genre.name());
        }
    }

    private void appendSort(StringBuilder sql, String sort) {
        if (sort != null) {
            boolean desc = sort.endsWith(":desc");
            String field = sort.replace(":desc", "");
            switch (field) {
                case "id", "name", "oscarsCount" -> sql.append(" ORDER BY m.").append(field).append(desc ? " DESC" : " ASC");
                default -> sql.append(" ORDER BY m.id ASC");
            }
        } else {
            sql.append(" ORDER BY m.id ASC");
        }
    }

    private Long insertCoordinates(Connection conn, Coordinates coordinates) throws SQLException {
        if (coordinates == null) return null;
        String sql = "INSERT INTO coordinates (x, y) VALUES (?, ?) RETURNING id";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, coordinates.x());
            stmt.setDouble(2, coordinates.y());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getLong("id");
            }
        }
        return null;
    }

    private void updateCoordinates(Connection conn, Coordinates coordinates) throws SQLException {
        if (coordinates == null || coordinates.id() == null) return;
        String sql = "UPDATE coordinates SET x = ?, y = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, coordinates.x());
            stmt.setDouble(2, coordinates.y());
            stmt.setLong(3, coordinates.id());
            stmt.executeUpdate();
        }
    }

    private Long insertPerson(Connection conn, Person person) throws SQLException {
        if (person == null) return null;
        String sql = "INSERT INTO person (name, birthday, height, weight, passport_id) VALUES (?, ?, ?, ?, ?) RETURNING id";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, person.name());
            stmt.setDate(2, person.birthday() != null ? Date.valueOf(person.birthday()) : null);
            stmt.setDouble(3, person.height());
            stmt.setLong(4, person.weight());
            stmt.setString(5, person.passportID());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getLong("id");
            }
        }
        return null;
    }

    private void updatePerson(Connection conn, Person person) throws SQLException {
        if (person == null || person.id() == null) return;
        String sql = "UPDATE person SET name = ?, birthday = ?, height = ?, weight = ?, passport_id = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, person.name());
            stmt.setDate(2, person.birthday() != null ? Date.valueOf(person.birthday()) : null);
            stmt.setDouble(3, person.height());
            stmt.setLong(4, person.weight());
            stmt.setString(5, person.passportID());
            stmt.setLong(6, person.id());
            stmt.executeUpdate();
        }
    }

    private Movie mapResultSetToMovie(ResultSet rs) throws SQLException {
        Movie movie = new Movie();
        movie.setId(rs.getLong("id"));
        movie.setName(rs.getString("name"));
        movie.setOscarsCount(rs.getObject("oscars_count", Long.class));
        movie.setGoldenPalmCount(rs.getObject("golden_palm_count", Long.class));
        movie.setBudget(rs.getObject("budget", Float.class));
        movie.setGenre(MovieGenre.valueOf(rs.getString("genre")));

        Long coordId = rs.getObject("coordinates_id", Long.class);
        Long x = rs.getObject("coord_x", Long.class);
        Double y = rs.getObject("coord_y", Double.class);
        if (x != null && y != null) {
            movie.setCoordinates(new Coordinates(coordId, x, y));
        }

        Long personId = rs.getObject("screenwriter_id", Long.class);
        String name = rs.getString("p_name");
        if (name != null) {
            movie.setScreenwriter(new Person(
                    personId,
                    name,
                    rs.getDate("p_birthday") != null ? rs.getDate("p_birthday").toLocalDate() : null,
                    rs.getDouble("p_height"),
                    rs.getLong("p_weight"),
                    rs.getString("p_passport")
            ));
        }

        return movie;
    }
}