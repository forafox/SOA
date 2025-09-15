package com.jellyone.oscars.testdata;

import com.jellyone.oscars.model.Coordinates;
import com.jellyone.oscars.model.Movie;
import com.jellyone.oscars.model.MovieGenre;
import com.jellyone.oscars.model.Person;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TestDataProvider {

    public static final String TEST_MOVIE_JSON = """
            {
                "id": 1,
                "name": "Test Movie",
                "coordinates": {
                    "x": 10,
                    "y": 120.5
                },
                "creationDate": "2024-01-01",
                "oscarsCount": 0,
                "goldenPalmCount": 1,
                "budget": 1000000.50,
                "genre": "ACTION",
                "screenwriter": {
                    "name": "Test Writer",
                    "birthday": "2000-01-01",
                    "height": 1.7,
                    "weight": 70,
                    "passportID": "TEST123"
                }
            }
            """;

    public static final String TEST_MOVIE_WITH_OSCARS_JSON = """
            {
                "id": 1,
                "name": "Test Movie",
                "coordinates": {
                    "x": 10,
                    "y": 120.5
                },
                "creationDate": "2024-01-01",
                "oscarsCount": 3,
                "goldenPalmCount": 1,
                "budget": 1000000.50,
                "genre": "ACTION",
                "screenwriter": {
                    "name": "Test Writer",
                    "birthday": "2000-01-01",
                    "height": 1.7,
                    "weight": 70,
                    "passportID": "TEST123"
                }
            }
            """;

    public static Movie createTestMovie() {
        return new Movie(
                1L,
                "Test Movie",
                new Coordinates(10, 120.5),
                LocalDate.of(2024, 1, 1),
                0,
                1,
                BigDecimal.valueOf(1000000.50),
                MovieGenre.ACTION,
                new Person(
                        "Test Writer",
                        LocalDate.of(2000, 1, 1),
                        1.7,
                        70,
                        "TEST123"
                )
        );
    }

    public static Movie createTestMovieWithOscars() {
        return new Movie(
                1L,
                "Test Movie",
                new Coordinates(10, 120.5),
                LocalDate.of(2024, 1, 1),
                3,
                1,
                BigDecimal.valueOf(1000000.50),
                MovieGenre.ACTION,
                new Person(
                        "Test Writer",
                        LocalDate.of(2000, 1, 1),
                        1.7,
                        70,
                        "TEST123"
                )
        );
    }

    public static Person createTestPerson() {
        return new Person(
                "Test Person",
                LocalDate.of(1990, 5, 15),
                1.75,
                65,
                "PASS123"
        );
    }
}
