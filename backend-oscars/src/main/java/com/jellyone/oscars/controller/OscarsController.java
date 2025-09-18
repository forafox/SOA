package com.jellyone.oscars.controller;

import com.jellyone.oscars.model.MovieUpdateResponse;
import com.jellyone.oscars.model.Person;
import com.jellyone.oscars.service.OscarsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.callbacks.Callback;
import io.swagger.v3.oas.annotations.callbacks.Callbacks;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Tag(name = "Oscars", description = "Дополнительные операции с наградами и статистикой Оскаров")
public class OscarsController {
    private final OscarsService service;

    @GetMapping("/oscars/operators/losers")
    @Operation(
            summary = "Получить операторов без Оскаров",
            operationId = "getOscarLosers"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Список операторов без Оскаров",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "array", implementation = Person.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "204",
                    description = "Операторов без Оскаров нет"
            )
    })
    public ResponseEntity<List<Person>> getOscarLosers() {
        System.out.println("OscarsController: Getting Oscar losers...");
        List<Person> losers = service.getOscarLosers();
        System.out.println("OscarsController: Returning " + losers.size() + " Oscar losers");
        return losers.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(losers);
    }

    @PostMapping("/oscars/movies/honor-by-length/{minLength}")
    @Operation(
            summary = "Дополнительно наградить фильмы с длиной > minLength",
            operationId = "honorMoviesByLength"
    )
    @Callbacks({
            @Callback(
                    name = "onAwarded",
                    callbackUrlExpression = "{$request.body#/updatedMovies}",
                    operation = @Operation(
                            summary = "Callback для уведомления о награждении",
                            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                    required = true,
                                    content = @Content(
                                            mediaType = "application/json",
                                            schema = @Schema(
                                                    type = "object",
                                                    description = "Данные для callback уведомления о награждении"
                                            )
                                    )
                            ),
                            responses = @ApiResponse(
                                    responseCode = "200",
                                    description = "Callback принят успешно"
                            )
                    )
            )
    })
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Фильмы успешно обновлены",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MovieUpdateResponse.class)
                    )
            )
    })
    public ResponseEntity<MovieUpdateResponse> honorMoviesByLength(
            @Parameter(description = "Минимальная длина фильма", required = true, schema = @Schema(type = "number", minimum = "0"))
            @PathVariable double minLength,
            @Parameter(description = "Количество Оскаров для добавления", required = true, schema = @Schema(type = "integer", minimum = "1"))
            @RequestParam int oscarsToAdd,
            @RequestBody(required = false) Map<String, Object> body
    ) {
        String callbackUrl = body == null ? null : (String) body.get("callbackUrl");
        Map<String, Object> result = service.honorMoviesByLength(minLength, oscarsToAdd, callbackUrl);
        
        Integer updatedCount = (Integer) result.get("updatedCount");
        @SuppressWarnings("unchecked")
        List<com.jellyone.oscars.model.Movie> updatedMovies = (List<com.jellyone.oscars.model.Movie>) result.get("updatedMovies");
        
        return ResponseEntity.ok(new MovieUpdateResponse(updatedCount, updatedMovies));
    }

    @PostMapping("/oscars/movies/honor-low-oscars")
    @Operation(
            summary = "Наградить фильмы с минимальным количеством Оскаров",
            operationId = "honorMoviesWithFewOscars"
    )
    @Callbacks({
            @Callback(
                    name = "notifyAdmins",
                    callbackUrlExpression = "{$request.body#/updatedMovies}",
                    operation = @io.swagger.v3.oas.annotations.Operation(
                            summary = "Callback для уведомления администраторов",
                            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                    required = true,
                                    content = @Content(
                                            mediaType = "application/json",
                                            schema = @Schema(
                                                    type = "object",
                                                    description = "Данные для callback уведомления администраторов"
                                            )
                                    )
                            ),
                            responses = @ApiResponse(
                                    responseCode = "200",
                                    description = "Callback принят успешно"
                            )
                    )
            )
    })
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Фильмы успешно обновлены",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MovieUpdateResponse.class)
                    )
            )
    })
    public ResponseEntity<MovieUpdateResponse> honorMoviesWithFewOscars(
            @Parameter(description = "Максимальное количество Оскаров", required = true, schema = @Schema(type = "integer", minimum = "1"))
            @RequestParam int maxOscars,
            @Parameter(description = "Количество Оскаров для добавления", required = true, schema = @Schema(type = "integer", minimum = "1"))
            @RequestParam int oscarsToAdd,
            @RequestBody(required = false) Map<String, Object> body
    ) {
        String callbackUrl = body == null ? null : (String) body.get("callbackUrl");
        Map<String, Object> result = service.honorMoviesWithFewOscars(maxOscars, oscarsToAdd, callbackUrl);
        
        Integer updatedCount = (Integer) result.get("updatedCount");
        @SuppressWarnings("unchecked")
        List<com.jellyone.oscars.model.Movie> updatedMovies = (List<com.jellyone.oscars.model.Movie>) result.get("updatedMovies");
        
        return ResponseEntity.ok(new MovieUpdateResponse(updatedCount, updatedMovies));
    }

    @GetMapping("/oscars/movies/{movieId}")
    @Operation(
            summary = "Получить все Оскары по фильму",
            operationId = "getOscarsByMovie"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Список Оскаров по фильму",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "array")
                    )
            ),
            @ApiResponse(
                    responseCode = "204",
                    description = "Оскаров для фильма нет"
            )
    })
    public ResponseEntity<List<Map<String, Object>>> getOscarsByMovie(
            @Parameter(description = "ID фильма", required = true, schema = @Schema(type = "integer", minimum = "1"))
            @PathVariable long movieId,
            @Parameter(description = "Номер страницы", schema = @Schema(type = "integer", minimum = "1", defaultValue = "1"))
            @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "Размер страницы", schema = @Schema(type = "integer", minimum = "1", maximum = "100", defaultValue = "20"))
            @RequestParam(defaultValue = "20") int size
    ) {
        List<Map<String, Object>> oscars = service.getOscarsByMovie(movieId, page, size);
        return oscars.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(oscars);
    }

    @PostMapping("/oscars/movies/{movieId}")
    @Operation(
            summary = "Наградить фильм",
            operationId = "addOscars"
    )
    @Callbacks({
            @Callback(
                    name = "notifyOscarsTeam",
                    callbackUrlExpression = "{$request.body}",
                    operation = @io.swagger.v3.oas.annotations.Operation(
                            summary = "Callback для уведомления команды Оскаров",
                            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                    required = true,
                                    content = @Content(
                                            mediaType = "application/json",
                                            schema = @Schema(
                                                    type = "object",
                                                    description = "Данные для callback уведомления команды Оскаров"
                                            )
                                    )
                            ),
                            responses = @ApiResponse(
                                    responseCode = "200",
                                    description = "Callback принят успешно"
                            )
                    )
            )
    })
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Фильм успешно обновлен",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MovieUpdateResponse.class)
                    )
            )
    })
    public ResponseEntity<MovieUpdateResponse> addOscars(
            @Parameter(description = "ID фильма", required = true, schema = @Schema(type = "integer", minimum = "1"))
            @PathVariable long movieId,
            @Parameter(description = "Количество Оскаров для добавления", required = true, schema = @Schema(type = "integer", minimum = "1"))
            @RequestParam int oscarsToAdd,
            @RequestBody(required = false) Map<String, Object> body
    ) {
        String callbackUrl = body == null ? null : (String) body.get("callbackUrl");
        Map<String, Object> result = service.addOscars(movieId, oscarsToAdd, callbackUrl);
        
        Integer updatedCount = (Integer) result.get("updatedCount");
        @SuppressWarnings("unchecked")
        List<com.jellyone.oscars.model.Movie> updatedMovies = (List<com.jellyone.oscars.model.Movie>) result.get("updatedMovies");
        
        return ResponseEntity.ok(new MovieUpdateResponse(updatedCount, updatedMovies));
    }

    @DeleteMapping("/oscars/movies/{movieId}")
    @Operation(
            summary = "Удалить все Оскары по фильму",
            operationId = "deleteOscarsByMovie"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Все Оскары для фильма успешно удалены"
            ),
            @ApiResponse(
                    responseCode = "304",
                    description = "Фильм не найден или Оскаров нет"
            )
    })
    public ResponseEntity<Void> deleteOscarsByMovie(
            @Parameter(description = "ID фильма", required = true, schema = @Schema(type = "integer", minimum = "1"))
            @PathVariable long movieId
    ) {
        boolean changed = service.deleteOscarsByMovie(movieId);
        return changed
                ? ResponseEntity.noContent().build()
                : ResponseEntity.status(304).build();
    }
}


