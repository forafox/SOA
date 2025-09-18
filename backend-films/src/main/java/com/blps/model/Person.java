package com.blps.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record Person(
    Long id,
    @NotBlank
    String name,
    LocalDate birthday,
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
