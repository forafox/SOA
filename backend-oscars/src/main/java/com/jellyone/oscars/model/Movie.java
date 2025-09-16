package com.jellyone.oscars.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Фильм")
public record Movie(
        @Schema(description = "Уникальный идентификатор фильма", example = "1", minimum = "1")
        Long id,
        
        @Schema(description = "Название фильма", example = "The Matrix", minLength = 1)
        String name,
        
        @Schema(description = "Координаты фильма")
        Coordinates coordinates,
        
        @Schema(description = "Дата создания фильма", example = "2025-09-16")
        LocalDate creationDate,
        
        @Schema(description = "Количество Оскаров", example = "3", minimum = "1")
        Integer oscarsCount,
        
        @Schema(description = "Количество Золотых пальмовых ветвей", example = "1", minimum = "1")
        Integer goldenPalmCount,
        
        @Schema(description = "Бюджет фильма", example = "1000000.50", minimum = "0", exclusiveMinimum = true)
        BigDecimal budget,
        
        @Schema(description = "Жанр фильма")
        MovieGenre genre,
        
        @Schema(description = "Сценарист фильма")
        Person screenwriter
) {}


