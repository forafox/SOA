package com.blps.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record Person(
        Long id,
        @NotBlank
        String name,
        LocalDate birthday,
        @Positive
        double height,
        @Positive
        long weight,
        @NotBlank
        String passportID
) {

}
