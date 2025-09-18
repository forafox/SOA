package com.blps.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDate;

public class Movie {

    @NotNull
    @Positive
    private Long id;
    
    @NotBlank
    private String name;
    
    @NotNull
    private Coordinates coordinates;
    
    private LocalDate creationDate = LocalDate.now();
    
    @PositiveOrZero
    private Long oscarsCount;
    
    @PositiveOrZero
    private Long goldenPalmCount;
    
    @Positive
    private Float budget;
    
    @NotNull
    private MovieGenre genre;
    
    private Person screenwriter;

    public Movie() {}

    public Movie(Long id, String name, Coordinates coordinates, LocalDate creationDate, 
                 Long oscarsCount, Long goldenPalmCount, Float budget, MovieGenre genre, Person screenwriter) {
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = creationDate;
        this.oscarsCount = oscarsCount;
        this.goldenPalmCount = goldenPalmCount;
        this.budget = budget;
        this.genre = genre;
        this.screenwriter = screenwriter;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("name cannot be null or empty");
        }
        this.name = name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public Long getOscarsCount() {
        return oscarsCount;
    }

    public void setOscarsCount(Long oscarsCount) {
        if (oscarsCount != null && oscarsCount < 0) {
            throw new IllegalArgumentException("oscarsCount must be non-negative");
        }
        this.oscarsCount = oscarsCount;
    }

    public Long getGoldenPalmCount() {
        return goldenPalmCount;
    }

    public void setGoldenPalmCount(Long goldenPalmCount) {
        if (goldenPalmCount != null && goldenPalmCount < 0) {
            throw new IllegalArgumentException("goldenPalmCount must be non-negative");
        }
        this.goldenPalmCount = goldenPalmCount;
    }

    public Float getBudget() {
        return budget;
    }

    public void setBudget(Float budget) {
        if (budget != null && budget <= 0) {
            throw new IllegalArgumentException("budget must be positive");
        }
        this.budget = budget;
    }

    public MovieGenre getGenre() {
        return genre;
    }

    public void setGenre(MovieGenre genre) {
        this.genre = genre;
    }

    public Person getScreenwriter() {
        return screenwriter;
    }

    public void setScreenwriter(Person screenwriter) {
        this.screenwriter = screenwriter;
    }
}
