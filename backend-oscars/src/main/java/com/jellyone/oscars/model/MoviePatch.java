package com.jellyone.oscars.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Данные для частичного обновления фильма")
public record MoviePatch(
        @Schema(description = "Название фильма", example = "The Matrix Reloaded")
        String name,
        
        @Schema(description = "Координаты фильма")
        Coordinates coordinates,
        
        @Schema(description = "Количество Оскаров", example = "5", minimum = "1")
        Integer oscarsCount,
        
        @Schema(description = "Количество Золотых пальмовых ветвей", example = "2", minimum = "1")
        Integer goldenPalmCount,
        
        @Schema(description = "Бюджет фильма", example = "2000000.75", minimum = "0", exclusiveMinimum = true)
        BigDecimal budget,
        
        @Schema(description = "Жанр фильма")
        MovieGenre genre,
        
        @Schema(description = "Сценарист фильма")
        Person screenwriter
) {}
