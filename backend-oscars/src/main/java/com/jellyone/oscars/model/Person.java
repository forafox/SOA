package com.jellyone.oscars.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "Персона (оператор, сценарист и т.д.)")
public record Person(
        @Schema(description = "Имя персоны", example = "John Doe", minLength = 1)
        String name,
        
        @Schema(description = "Дата рождения", example = "1980-01-01")
        LocalDate birthday,
        
        @Schema(description = "Рост в сантиметрах", example = "180.5", minimum = "0", exclusiveMinimum = true)
        Double height,
        
        @Schema(description = "Вес в килограммах", example = "75", minimum = "1")
        Integer weight,
        
        @Schema(description = "Номер паспорта", example = "123456789", minLength = 1)
        String passportID
) {}


