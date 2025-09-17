package com.blps;

import com.blps.DAO.MovieDAO;
import com.blps.entity.Movie;
import com.blps.entity.MovieGenre;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Path("/movies")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MovieResource {


    private final MovieDAO movieDAO = new MovieDAO();

    @GET
    @Path("/health")
    public Response health() {
        return Response.ok("OK").build();
    }


    @POST
    public Response create(Movie movie) {
        try {
            Movie created = movieDAO.create(movie);
            return Response.status(Response.Status.CREATED).entity(created).build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.serverError().build();
        }
    }

    @GET
    @Path("/{id}")
    public Response get(@PathParam("id") Long id) {
        try {
            Movie movie = movieDAO.getById(id);
            if (movie == null) return Response.noContent().build();
            return Response.ok(movie).build();
        } catch (SQLException e) {
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
        try {
            List<Movie> result = movieDAO.getAll();

            // фильтр по имени
            if (name != null && !name.isEmpty()) {
                result = result.stream()
                        .filter(m -> m.getName() != null && m.getName().contains(name))
                        .collect(Collectors.toList());
            }

            // фильтр по жанру
            if (genre != null) {
                result = result.stream()
                        .filter(m -> genre.equals(m.getGenre()))
                        .collect(Collectors.toList());
            }

            // сортировка
            if (sort != null) {
                boolean desc = sort.contains(":desc");
                String field = sort.replace(":desc", "");

                Comparator<Movie> comparator;
                switch (field) {
                    case "oscarsCount":
                        comparator = Comparator.comparing(Movie::getOscarsCount, Comparator.nullsLast(Long::compareTo));
                        break;
                    case "id":
                        comparator = Comparator.comparing(Movie::getId, Comparator.nullsLast(Long::compareTo));
                        break;
                    case "name":
                        comparator = Comparator.comparing(Movie::getName, Comparator.nullsLast(String::compareTo));
                        break;
                    default:
                        comparator = Comparator.comparing(Movie::getId, Comparator.nullsLast(Long::compareTo));
                        break;
                }
                if (desc) comparator = comparator.reversed();
                result = result.stream().sorted(comparator).collect(Collectors.toList());
            }

            // пагинация
            int fromIndex = (page - 1) * size;
            if (fromIndex >= result.size()) return Response.ok(Collections.emptyList()).build();
            int toIndex = Math.min(fromIndex + size, result.size());
            result = result.subList(fromIndex, toIndex);

            return Response.ok(result).build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.serverError().build();
        }
    }

    @PATCH
    @Path("/{id}")
    public Response patch(@PathParam("id") Long id, Movie partialMovie) {
        try {
            Movie existing = movieDAO.getById(id);
            if (existing == null) return Response.noContent().build();

            // обновляем поля
            if (partialMovie.getName() != null) existing.setName(partialMovie.getName());
            if (partialMovie.getCoordinates() != null) existing.setCoordinates(partialMovie.getCoordinates());
            if (partialMovie.getOscarsCount() != null) existing.setOscarsCount(partialMovie.getOscarsCount());
            if (partialMovie.getGoldenPalmCount() != null)
                existing.setGoldenPalmCount(partialMovie.getGoldenPalmCount());
            if (partialMovie.getBudget() != null) existing.setBudget(partialMovie.getBudget());
            if (partialMovie.getGenre() != null) existing.setGenre(partialMovie.getGenre());
            if (partialMovie.getScreenwriter() != null) existing.setScreenwriter(partialMovie.getScreenwriter());

            Movie updated = movieDAO.update(existing);
            return Response.ok(updated).build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.serverError().build();
        }
    }


    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        try {
            boolean deleted = movieDAO.deleteById(id);
            return deleted ? Response.noContent().build() : Response.notModified().build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.serverError().build();
        }
    }


    @DELETE
    @Path("/oscarsCount/{count}")
    public Response deleteByOscarsCount(@PathParam("count") int count) {
        try {
            List<Movie> all = movieDAO.getAll();
            boolean anyDeleted = false;
            for (Movie m : all) {
                if (m.getOscarsCount() != null && m.getOscarsCount() == count) {
                    movieDAO.deleteById(m.getId());
                    anyDeleted = true;
                }
            }
            return anyDeleted ? Response.noContent().build() : Response.notModified().build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.serverError().build();
        }
    }


    @GET
    @Path("/count/oscars-less-than/{count}")
    public Response countMoviesWithOscarsLessThan(@PathParam("count") int count) {
        try {
            long cnt = movieDAO.getAll().stream()
                    .filter(m -> m.getOscarsCount() != null && m.getOscarsCount() < count)
                    .count();
            Map<String, Object> response = new HashMap<>();
            response.put("count", cnt);
            return Response.ok(response).build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.serverError().build();
        }
    }


    @GET
    @Path("/name-starts-with/{prefix}")
    public Response getMoviesByNamePrefix(@PathParam("prefix") String prefix) {
        try {
            List<Movie> result = movieDAO.getAll().stream()
                    .filter(m -> m.getName() != null && m.getName().startsWith(prefix))
                    .collect(Collectors.toList());
            return Response.ok(result).build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.serverError().build();
        }
    }
}