package com.blps.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Movie {

    @Setter
    @NotNull
    @Positive
    private Long id;

    @NotBlank
    private String name;

    @Setter
    @NotNull
    private Coordinates coordinates;

    @Setter
    private LocalDate creationDate = LocalDate.now();

    @PositiveOrZero
    private Long oscarsCount;

    @PositiveOrZero
    private Long goldenPalmCount;

    @Positive
    private Float budget;

    @Setter
    @NotNull
    private MovieGenre genre;

    @Setter
    private Person screenwriter;

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("name cannot be null or empty");
        }
        this.name = name;
    }

    public void setOscarsCount(Long oscarsCount) {
        if (oscarsCount != null && oscarsCount < 0) {
            throw new IllegalArgumentException("oscarsCount must be non-negative");
        }
        this.oscarsCount = oscarsCount;
    }

    public void setGoldenPalmCount(Long goldenPalmCount) {
        if (goldenPalmCount != null && goldenPalmCount < 0) {
            throw new IllegalArgumentException("goldenPalmCount must be non-negative");
        }
        this.goldenPalmCount = goldenPalmCount;
    }

    public void setBudget(Float budget) {
        if (budget != null && budget <= 0) {
            throw new IllegalArgumentException("budget must be positive");
        }
        this.budget = budget;
    }

}
