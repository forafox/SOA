package com.jellyone.oscars.controller;

import com.jellyone.oscars.model.Person;
import com.jellyone.oscars.service.OscarsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
        List<Person> losers = service.getOscarLosers();
        return losers.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(losers);
    }

    @PostMapping("/oscars/movies/honor-by-length/{minLength}")
    @Operation(
            summary = "Дополнительно наградить фильмы с длиной > minLength",
            operationId = "honorMoviesByLength"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Фильмы успешно обновлены",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "object")
                    )
            )
    })
    public ResponseEntity<Map<String, Object>> honorMoviesByLength(
            @Parameter(description = "Минимальная длина фильма", required = true, schema = @Schema(type = "number", minimum = "0"))
            @PathVariable double minLength,
            @Parameter(description = "Количество Оскаров для добавления", required = true, schema = @Schema(type = "integer", minimum = "1"))
            @RequestParam int oscarsToAdd,
            @RequestBody(required = false) Map<String, Object> body
    ) {
        String callbackUrl = body == null ? null : (String) body.get("callbackUrl");
        Map<String, Object> result = service.honorMoviesByLength(minLength, oscarsToAdd, callbackUrl);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/oscars/movies/honor-low-oscars")
    @Operation(
            summary = "Наградить фильмы с минимальным количеством Оскаров",
            operationId = "honorMoviesWithFewOscars"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Фильмы успешно обновлены",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "object")
                    )
            )
    })
    public ResponseEntity<Map<String, Object>> honorMoviesWithFewOscars(
            @Parameter(description = "Максимальное количество Оскаров", required = true, schema = @Schema(type = "integer", minimum = "1"))
            @RequestParam int maxOscars,
            @Parameter(description = "Количество Оскаров для добавления", required = true, schema = @Schema(type = "integer", minimum = "1"))
            @RequestParam int oscarsToAdd,
            @RequestBody(required = false) Map<String, Object> body
    ) {
        String callbackUrl = body == null ? null : (String) body.get("callbackUrl");
        Map<String, Object> result = service.honorMoviesWithFewOscars(maxOscars, oscarsToAdd, callbackUrl);
        return ResponseEntity.ok(result);
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
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Фильм успешно обновлен",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "object")
                    )
            )
    })
    public ResponseEntity<Map<String, Object>> addOscars(
            @Parameter(description = "ID фильма", required = true, schema = @Schema(type = "integer", minimum = "1"))
            @PathVariable long movieId,
            @Parameter(description = "Количество Оскаров для добавления", required = true, schema = @Schema(type = "integer", minimum = "1"))
            @RequestParam int oscarsToAdd,
            @RequestBody(required = false) Map<String, Object> body
    ) {
        String callbackUrl = body == null ? null : (String) body.get("callbackUrl");
        Map<String, Object> result = service.addOscars(movieId, oscarsToAdd, callbackUrl);
        return ResponseEntity.ok(result);
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


