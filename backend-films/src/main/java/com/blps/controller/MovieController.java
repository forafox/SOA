package com.blps.controller;

import com.blps.config.PATCH;
import com.blps.model.Movie;
import com.blps.model.MovieGenre;
import com.blps.repository.impl.MovieRepositoryImpl;
import com.blps.service.MovieService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.sql.SQLException;
import lombok.extern.slf4j.Slf4j;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/movies")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Slf4j
public class MovieController {

    private final MovieService movieService = new MovieService(new MovieRepositoryImpl());

    @POST
    public Response create(Movie movie) {
        log.info("Creating movie - {}", movie.getName());
        try {
            Movie created = movieService.createMovie(movie);
            log.info("Movie created successfully with ID: {}", created.getId());
            return Response.status(Response.Status.CREATED).entity(created).build();
        } catch (SQLException e) {
            log.error("Error creating movie", e);
            return Response.serverError().build();
        }
    }

    @GET
    @Path("/{id}")
    public Response get(@PathParam("id") Long id) {
        log.info("Getting movie by ID - {}", id);
        try {
            Movie movie = movieService.getMovieById(id);
            if (movie == null) {
                log.info("Movie not found");
                return Response.noContent().build();
            }
            log.info("Movie found - {}", movie.getName());
            return Response.ok(movie).build();
        } catch (SQLException e) {
            log.error("Error getting movie", e);
            return Response.serverError().build();
        }
    }

    @GET
    public Response getMovies(@QueryParam("name") String name,
                              @QueryParam("genre") MovieGenre genre,
                              @QueryParam("sort") String sort,
                              @QueryParam("page") @DefaultValue("1") int page,
                              @QueryParam("size") @DefaultValue("20") int size) {
        log.info("Getting movies with filters");
        try {
            List<Movie> result = movieService.getMovies(name, genre, sort, page, size);
            log.info("Returning {} movies", result.size());
            return Response.ok(result).build();
        } catch (SQLException e) {
            log.error("Error getting movies", e);
            return Response.serverError().build();
        }
    }

    @PATCH
    @Path("/{id}")
    public Response patch(@PathParam("id") Long id, Movie partialMovie) {
        log.info("Patching movie with ID - {}", id);
        try {
            Movie updated = movieService.updateMovie(id, partialMovie);
            if (updated == null) {
                log.info("Movie not found for update");
                return Response.noContent().build();
            }
            log.info("Movie updated successfully");
            return Response.ok(updated).build();
        } catch (SQLException e) {
            log.error("Error updating movie", e);
            return Response.serverError().build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response put(@PathParam("id") Long id, Movie partialMovie) {
        log.info("Updating movie with ID - {} via PUT", id);
        try {
            Movie updated = movieService.updateMovie(id, partialMovie);
            if (updated == null) {
                log.info("Movie not found for update");
                return Response.noContent().build();
            }
            log.info("Movie updated successfully via PUT");
            return Response.ok(updated).build();
        } catch (SQLException e) {
            log.error("Error updating movie via PUT", e);
            return Response.serverError().build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        log.info("Deleting movie with ID - {}", id);
        try {
            boolean deleted = movieService.deleteMovie(id);
            log.info("Movie deletion result: {}", deleted);
            return deleted ? Response.noContent().build() : Response.notModified().build();
        } catch (SQLException e) {
            log.error("Error deleting movie", e);
            return Response.serverError().build();
        }
    }

    @DELETE
    @Path("/oscarsCount/{count}")
    public Response deleteByOscarsCount(@PathParam("count") int count) {
        log.info("Deleting movies with oscars count - {}", count);
        try {
            boolean anyDeleted = movieService.deleteMoviesByOscarsCount(count);
            log.info("Movies deleted with oscars count {}: {}", count, anyDeleted);
            return anyDeleted ? Response.noContent().build() : Response.notModified().build();
        } catch (SQLException e) {
            log.error("Error deleting movies by oscars count", e);
            return Response.serverError().build();
        }
    }

    @GET
    @Path("/count/oscars-less-than/{count}")
    public Response countMoviesWithOscarsLessThan(@PathParam("count") int count) {
        log.info("Counting movies with oscars less than - {}", count);
        try {
            long cnt = movieService.countMoviesWithOscarsLessThan(count);
            Map<String, Object> response = new HashMap<>();
            response.put("count", cnt);
            log.info("Count result: {}", cnt);
            return Response.ok(response).build();
        } catch (SQLException e) {
            log.error("Error counting movies", e);
            return Response.serverError().build();
        }
    }

    @GET
    @Path("/name-starts-with/{prefix}")
    public Response getMoviesByNamePrefix(@PathParam("prefix") String prefix) {
        log.info("Getting movies with name prefix - {}", prefix);
        try {
            List<Movie> result = movieService.getMoviesByNamePrefix(prefix);
            log.info("Found {} movies with prefix {}", result.size(), prefix);
            return Response.ok(result).build();
        } catch (SQLException e) {
            log.error("Error getting movies by prefix", e);
            return Response.serverError().build();
        }
    }
}
