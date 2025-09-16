package com.jellyone.oscars.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Ответ на обновление фильмов")
public record MovieUpdateResponse(
        @Schema(description = "Количество обновленных фильмов", example = "5")
        Integer updatedCount,
        
        @Schema(description = "Список обновленных фильмов")
        List<Movie> updatedMovies
) {}
