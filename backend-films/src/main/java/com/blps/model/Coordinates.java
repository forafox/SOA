package com.blps.model;

import jakarta.validation.constraints.NotNull;

public record Coordinates(
    Long id,
    @NotNull Long x,
    @NotNull Double y
) {
    public Coordinates {
        if (x == null) {
            throw new IllegalArgumentException("x coordinate cannot be null");
        }
        if (y == null) {
            throw new IllegalArgumentException("y coordinate cannot be null");
        }
    }
}
