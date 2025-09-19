package com.blps.controller;

import com.blps.config.PATCH;
import com.blps.model.Movie;
import com.blps.model.MovieGenre;
import com.blps.repository.impl.MovieRepositoryImpl;
import com.blps.service.MovieService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Path("/movies")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MovieController {

    private final MovieService movieService = new MovieService(new MovieRepositoryImpl());

    @POST
    public Response create(Movie movie) {
        log.info("Controller: Creating movie - {}", movie.getName());
        try {
            Movie created = movieService.createMovie(movie);
            log.info("Controller: Movie created successfully with ID: {}", created.getId());
            return Response.status(Response.Status.CREATED).entity(created).build();
        } catch (SQLException e) {
            log.error("Controller: Error creating movie: {}", e.getMessage());
            e.printStackTrace();
            return Response.serverError().build();
        }
    }

    @GET
    @Path("/{id}")
    public Response get(@PathParam("id") Long id) {
        log.info("Controller: Getting movie by ID - {}", id);
        try {
            Movie movie = movieService.getMovieById(id);
            if (movie == null) {
                log.info("Controller: Movie not found");
                return Response.noContent().build();
            }
            log.info("Controller: Movie found - {}", movie.getName());
            return Response.ok(movie).build();
        } catch (SQLException e) {
            log.error("Controller: Error getting movie: {}", e.getMessage());
            e.printStackTrace();
            return Response.serverError().build();
        }
    }

    @GET
    public Response getMovies(@QueryParam("name") String name,
                              @QueryParam("genre") MovieGenre genre,
                              @QueryParam("sort") String sort,
                              @QueryParam("page") @DefaultValue("1") int page,
                              @QueryParam("size") @DefaultValue("20") int size) {
        log.info("Controller: Getting movies with filters");
        try {
            List<Movie> result = movieService.getMovies(name, genre, sort, page, size);
            log.info("Controller: Returning {} movies", result.size());
            return Response.ok(result).build();
        } catch (SQLException e) {
            log.error("Controller: Error getting movies: {}", e.getMessage());
            e.printStackTrace();
            return Response.serverError().build();
        }
    }

    @PATCH
    @Path("/{id}")
    public Response patch(@PathParam("id") Long id, Movie partialMovie) {
        log.info("Controller: Patching movie with ID - {}", id);
        try {
            Movie updated = movieService.updateMovie(id, partialMovie);
            if (updated == null) {
                log.info("Controller: Movie not found for update");
                return Response.noContent().build();
            }
            log.info("Controller: Movie updated successfully");
            return Response.ok(updated).build();
        } catch (SQLException e) {
            log.error("Controller: Error updating movie: {}", e.getMessage());
            e.printStackTrace();
            return Response.serverError().build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response put(@PathParam("id") Long id, Movie partialMovie) {
        log.info("Controller: Updating movie with ID - {} via PUT", id);
        try {
            Movie updated = movieService.updateMovie(id, partialMovie);
            if (updated == null) {
                log.info("Controller: Movie not found for update");
                return Response.noContent().build();
            }
            log.info("Controller: Movie updated successfully via PUT");
            return Response.ok(updated).build();
        } catch (SQLException e) {
            log.error("Controller: Error updating movie via PUT: {}", e.getMessage());
            e.printStackTrace();
            return Response.serverError().build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        log.info("Controller: Deleting movie with ID - " + id);
        try {
            boolean deleted = movieService.deleteMovie(id);
            log.info("Controller: Movie deletion result: {}", deleted);
            return deleted ? Response.noContent().build() : Response.notModified().build();
        } catch (SQLException e) {
            log.error("Controller: Error deleting movie: {}", e.getMessage());
            e.printStackTrace();
            return Response.serverError().build();
        }
    }

    @DELETE
    @Path("/oscarsCount/{count}")
    public Response deleteByOscarsCount(@PathParam("count") int count) {
        log.info("Controller: Deleting movies with oscars count - {}", count);
        try {
            boolean anyDeleted = movieService.deleteMoviesByOscarsCount(count);
            log.info("Controller: Movies deleted with oscars count {}: {}", count, anyDeleted);
            return anyDeleted ? Response.noContent().build() : Response.notModified().build();
        } catch (SQLException e) {
            log.error("Controller: Error deleting movies by oscars count: {}", e.getMessage());
            e.printStackTrace();
            return Response.serverError().build();
        }
    }

    @GET
    @Path("/count/oscars-less-than/{count}")
    public Response countMoviesWithOscarsLessThan(@PathParam("count") int count) {
        log.info("Controller: Counting movies with oscars less than - {}", count);
        try {
            long cnt = movieService.countMoviesWithOscarsLessThan(count);
            Map<String, Object> response = new HashMap<>();
            response.put("count", cnt);
            log.info("Controller: Count result: {}", cnt);
            return Response.ok(response).build();
        } catch (SQLException e) {
            log.error("Controller: Error counting movies: {}", e.getMessage());
            e.printStackTrace();
            return Response.serverError().build();
        }
    }

    @GET
    @Path("/name-starts-with/{prefix}")
    public Response getMoviesByNamePrefix(@PathParam("prefix") String prefix) {
        log.info("Controller: Getting movies with name prefix - {}", prefix);
        try {
            List<Movie> result = movieService.getMoviesByNamePrefix(prefix);
            log.info("Controller: Found {} movies with prefix {}", result.size(), prefix);
            return Response.ok(result).build();
        } catch (SQLException e) {
            log.error("Controller: Error getting movies by prefix: {}", e.getMessage());
            e.printStackTrace();
            return Response.serverError().build();
        }
    }
}
