package com.blps.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;


@Getter
@Setter
public class Movie {

    @NotNull
    @Positive
    private Long id; // генерируется автоматически


    private String name; // not null, not empty


    private Coordinates coordinates; // not null


    private LocalDate creationDate = LocalDate.now(); // генерируется автоматически


    private long oscarsCount; // > 0

    private Long goldenPalmCount; // может быть null, > 0

    private Float budget; // может быть null, > 0


    private MovieGenre genre; // not null


    private Person screenwriter;
}