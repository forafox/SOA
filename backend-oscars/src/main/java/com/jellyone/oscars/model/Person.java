package com.jellyone.oscars.model;

import java.time.LocalDate;

public record Person(
        String name,
        LocalDate birthday,
        Double height,
        Integer weight,
        String passportID
) {}


