package com.jellyone.oscars.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Координаты фильма")
public record Coordinates(
        @Schema(description = "Координата X", example = "0")
        Integer x,
        
        @Schema(description = "Координата Y", example = "558", maximum = "558")
        Double y
) {}


