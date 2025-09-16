package com.jellyone.oscars.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Информация об ошибке")
public record Error(
        @Schema(description = "Сообщение об ошибке", example = "Validation failed")
        String message
) {
}
