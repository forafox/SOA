package com.jellyone.oscars.model;

import java.math.BigDecimal;

public record MoviePatch(
        String name,
        Coordinates coordinates,
        Integer oscarsCount,
        Integer goldenPalmCount,
        BigDecimal budget,
        MovieGenre genre,
        Person screenwriter
) {}
