package com.jellyone.oscars.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Информация об одной награде Оскар")
public record Award(
        @Schema(description = "Идентификатор награды в рамках фильма", example = "1")
        int awardId,

        @Schema(description = "Дата получения награды", example = "2024-01-01")
        String date,

        @Schema(description = "Категория награды", example = "Best Picture")
        String category
) {}


