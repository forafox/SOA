package com.blps.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Movie {

    @NotNull
    @Positive
    private Long id;

    @NotBlank
    private String name;

    @NotNull
    private Coordinates coordinates;

    private final LocalDate creationDate = LocalDate.now();

    @PositiveOrZero
    private Long oscarsCount;

    @PositiveOrZero
    private Long goldenPalmCount;

    @NotNull
    @Positive
    private Float budget;

    @NotNull
    private MovieGenre genre;

    private Person screenwriter;
}