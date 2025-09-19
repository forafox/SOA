package com.blps.model;

import jakarta.validation.constraints.NotNull;

public record Coordinates(
        Long id,
        @NotNull Long x,
        @NotNull Double y
) {
}
