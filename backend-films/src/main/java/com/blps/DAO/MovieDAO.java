package com.blps.DAO;

import com.blps.Database;
import com.blps.entity.Coordinates;
import com.blps.entity.Movie;
import com.blps.entity.MovieGenre;
import com.blps.entity.Person;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MovieDAO {


    public Movie create(Movie movie) throws SQLException {
        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);

            Long coordId = null;
            if (movie.getCoordinates() != null) {
                String sqlCoord = "INSERT INTO coordinates (x, y) VALUES (?, ?) RETURNING id";
                try (PreparedStatement stmt = conn.prepareStatement(sqlCoord)) {
                    stmt.setLong(1, movie.getCoordinates().getX());
                    stmt.setDouble(2, movie.getCoordinates().getY());
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        coordId = rs.getLong("id");
                        movie.getCoordinates().setId(coordId);
                    }
                }
            }


            Long personId = null;
            if (movie.getScreenwriter() != null) {
                Person p = movie.getScreenwriter();
                String sqlPerson = "INSERT INTO person (name, birthday, height, weight, passport_id) VALUES (?, ?, ?, ?, ?) RETURNING id";
                try (PreparedStatement stmt = conn.prepareStatement(sqlPerson)) {
                    stmt.setString(1, p.getName());
                    stmt.setDate(2, p.getBirthday() != null ? new java.sql.Date(p.getBirthday().getTime()) : null);
                    stmt.setDouble(3, p.getHeight());
                    stmt.setLong(4, p.getWeight());
                    stmt.setString(5, p.getPassportID());
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        personId = rs.getLong("id");
                        p.setId(personId);
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
        }

        return movie;
    }

    // ---------------- READ ----------------
    public Movie getById(Long id) throws SQLException {
        String sql = "SELECT m.*, c.x AS coord_x, c.y AS coord_y, " +
                "p.name AS p_name, p.birthday AS p_birthday, p.height AS p_height, p.weight AS p_weight, p.passport_id AS p_passport " +
                "FROM movies m " +
                "LEFT JOIN coordinates c ON m.coordinates_id = c.id " +
                "LEFT JOIN person p ON m.screenwriter_id = p.id " +
                "WHERE m.id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Movie movie = new Movie();
                movie.setId(rs.getLong("id"));
                movie.setName(rs.getString("name"));
                movie.setCreationDate(rs.getDate("creation_date").toLocalDate());
                movie.setOscarsCount(rs.getObject("oscars_count", Long.class));
                movie.setGoldenPalmCount(rs.getObject("golden_palm_count", Long.class));
                movie.setBudget(rs.getObject("budget", Float.class));
                movie.setGenre(MovieGenre.valueOf(rs.getString("genre")));

                // Coordinates
                Long coordId = rs.getObject("coordinates_id", Long.class); // добавь в SELECT: m.coordinates_id
                Long x = rs.getObject("coord_x", Long.class);
                Double y = rs.getObject("coord_y", Double.class);
                if (x != null && y != null) {
                    Coordinates coord = new Coordinates(coordId, x, y); // передаём id
                    movie.setCoordinates(coord);
                }

                // Screenwriter
                Long personId = rs.getObject("screenwriter_id", Long.class); // добавь в SELECT: m.screenwriter_id
                String name = rs.getString("p_name");
                if (name != null) {
                    Person p = new Person();
                    p.setId(personId);
                    p.setName(name);
                    p.setBirthday(rs.getDate("p_birthday"));
                    p.setHeight(rs.getDouble("p_height"));
                    p.setWeight(rs.getLong("p_weight"));
                    p.setPassportID(rs.getString("p_passport"));
                    movie.setScreenwriter(p);
                }

                return movie;
            } else return null;
        }
    }

    // ---------------- DELETE ----------------
    public boolean deleteById(Long id) throws SQLException {
        String sql = "DELETE FROM movies WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    // ---------------- UPDATE ----------------
    public Movie update(Movie movie) throws SQLException {
        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);

            // Обновление Coordinates
            if (movie.getCoordinates() != null) {
                String sqlCoord = "UPDATE coordinates SET x = ?, y = ? WHERE id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sqlCoord)) {
                    stmt.setLong(1, movie.getCoordinates().getX());
                    stmt.setDouble(2, movie.getCoordinates().getY());
                    stmt.setObject(3, movie.getCoordinates().getId());
                    stmt.executeUpdate();
                }
            }

            // Обновление Person
            if (movie.getScreenwriter() != null) {
                Person p = movie.getScreenwriter();
                String sqlPerson = "UPDATE person SET name = ?, birthday = ?, height = ?, weight = ?, passport_id = ? WHERE id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sqlPerson)) {
                    stmt.setString(1, p.getName());
                    stmt.setDate(2, p.getBirthday() != null ? new java.sql.Date(p.getBirthday().getTime()) : null);
                    stmt.setDouble(3, p.getHeight());
                    stmt.setLong(4, p.getWeight());
                    stmt.setString(5, p.getPassportID());
                    stmt.setObject(6, p.getId());
                    stmt.executeUpdate();
                }
            }

            // Обновление Movie
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
        }
        return movie;
    }

    // ---------------- LIST ----------------
    public List<Movie> getAll() throws SQLException {
        List<Movie> list = new ArrayList<>();
        String sql = "SELECT m.*, c.x AS coord_x, c.y AS coord_y, " +
                "p.name AS p_name, p.birthday AS p_birthday, p.height AS p_height, p.weight AS p_weight, p.passport_id AS p_passport " +
                "FROM movies m " +
                "LEFT JOIN coordinates c ON m.coordinates_id = c.id " +
                "LEFT JOIN person p ON m.screenwriter_id = p.id";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
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
                    Person p = new Person();
                    p.setId(personId);
                    p.setName(name);
                    p.setBirthday(rs.getDate("p_birthday"));
                    p.setHeight(rs.getDouble("p_height"));
                    p.setWeight(rs.getLong("p_weight"));
                    p.setPassportID(rs.getString("p_passport"));
                    movie.setScreenwriter(p);
                }

                list.add(movie);
            }
        }
        return list;
    }
}
