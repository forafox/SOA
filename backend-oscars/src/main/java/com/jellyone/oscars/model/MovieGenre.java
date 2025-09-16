package com.jellyone.oscars.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Жанр фильма")
public enum MovieGenre {
    @Schema(description = "Боевик")
    ACTION,
    
    @Schema(description = "Приключения")
    ADVENTURE,
    
    @Schema(description = "Трагедия")
    TRAGEDY,
    
    @Schema(description = "Фэнтези")
    FANTASY
}


