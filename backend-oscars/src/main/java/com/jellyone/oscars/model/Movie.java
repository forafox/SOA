package com.jellyone.oscars.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public record Movie(
        Long id,
        String name,
        Coordinates coordinates,
        LocalDate creationDate,
        Integer oscarsCount,
        Integer goldenPalmCount,
        BigDecimal budget,
        MovieGenre genre,
        Person screenwriter
) {}


