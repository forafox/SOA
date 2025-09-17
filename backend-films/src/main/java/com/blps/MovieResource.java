package com.blps;

import com.blps.entity.Movie;
import com.blps.entity.MovieGenre;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Path("/movies")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MovieResource {
    public static final Map<Long, Movie> movies = new HashMap<>();
    private static final AtomicLong idGenerator = new AtomicLong(1);


    @POST
    public Response create(Movie movie) {
        long id = idGenerator.getAndIncrement();
        movie.setId(id);
        movies.put(id, movie);
        return Response.status(Response.Status.CREATED).entity(movie).build();
    }
    @GET
    @Path("/{id}")
    public Response get(@PathParam("id") Long id) {
        Movie movie = movies.get(id);
        if (movie == null) {
            return Response.noContent().build(); // 204 по твоей спецификации
        }
        return Response.ok(movie).build();
    }


    @GET
    public Response getMovies(@QueryParam("name") String name,
                              @QueryParam("genre") MovieGenre genre,
                              @QueryParam("sort") String sort,
                              @QueryParam("page") @DefaultValue("1") int page,
                              @QueryParam("size") @DefaultValue("20") int size) {

        List<Movie> result = new ArrayList<>(movies.values());


        if (name != null && !name.isEmpty()) {
            result = result.stream()
                    .filter(m -> m.getName() != null && m.getName().contains(name))
                    .collect(Collectors.toList());
        }


        if (genre != null) {
            result = result.stream()
                    .filter(m -> genre.equals(m.getGenre()))
                    .collect(Collectors.toList());
        }


        if (sort != null) {
            boolean desc = sort.contains(":desc");
            String field = sort.replace(":desc", "");

            Comparator<Movie> comparator ;

            switch (field) {
                case "oscarsCount":
                    comparator = Comparator.comparing(
                            Movie::getOscarsCount,
                            Comparator.nullsLast(Long::compareTo)
                    );
                    break;
                case "id":
                    comparator = Comparator.comparing(
                            Movie::getId,
                            Comparator.nullsLast(Long::compareTo)
                    );
                    break;
                case "name":
                    comparator = Comparator.comparing(
                            Movie::getName,
                            Comparator.nullsLast(String::compareTo)
                    );
                    break;
                default:
                    comparator = Comparator.comparing(
                            Movie::getId,
                            Comparator.nullsLast(Long::compareTo)
                    );
                    break;
            }

            if (desc) comparator = comparator.reversed();

            result = result.stream()
                    .sorted(comparator)
                    .collect(Collectors.toList());
        }



        int fromIndex = (page - 1) * size;
        if (fromIndex >= result.size()) {
            return Response.ok(Collections.emptyList()).build();
        }
        int toIndex = Math.min(fromIndex + size, result.size());
        result = result.subList(fromIndex, toIndex);

        return Response.ok(result).build();
    }


    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        Movie removed = movies.remove(id);
        if (removed != null) {
            return Response.noContent().build();
        } else {
            return Response.notModified().build();
        }
    }


    @DELETE
    @Path("/oscarsCount/{count}")
    public Response deleteByOscarsCount(@PathParam("count") int count) {
        int before = movies.size();
        movies.values().removeIf(m -> m.getOscarsCount()  == count);
        int after = movies.size();
        if (before != after) {
            return Response.noContent().build();
        } else {
            return Response.notModified().build();
        }
    }


    @GET
    @Path("/count/oscars-less-than/{count}")
    public Response countMoviesWithOscarsLessThan(@PathParam("count") int count) {
        long cnt = movies.values().stream()
                .filter(m -> m.getOscarsCount()  < count)
                .count();
        Map<String, Object> response = new HashMap<>();
        response.put("count", cnt);
        return Response.ok(response).build();
    }


    @GET
    @Path("/name-starts-with/{prefix}")
    public Response getMoviesByNamePrefix(@PathParam("prefix") String prefix) {
        List<Movie> result = movies.values().stream()
                .filter(m -> m.getName() != null && m.getName().startsWith(prefix))
                .collect(Collectors.toList());
        return Response.ok(result).build();
    }
}