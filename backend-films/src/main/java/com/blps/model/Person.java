package com.blps.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.util.Date;

public record Person(
    Long id,
    @NotBlank
    String name,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    Date birthday,
    @Positive
    double height,
    @Positive
    long weight,
    @NotBlank
    String passportID
) {
    public Person {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("name cannot be null or empty");
        }
        if (height <= 0) {
            throw new IllegalArgumentException("height must be positive");
        }
        if (weight <= 0) {
            throw new IllegalArgumentException("weight must be positive");
        }
        if (passportID == null || passportID.trim().isEmpty()) {
            throw new IllegalArgumentException("passportID cannot be null or empty");
        }
    }
}
