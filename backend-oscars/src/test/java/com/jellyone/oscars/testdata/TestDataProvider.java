package com.jellyone.oscars.testdata;

import com.jellyone.oscars.model.Coordinates;
import com.jellyone.oscars.model.Movie;
import com.jellyone.oscars.model.MovieGenre;
import com.jellyone.oscars.model.Person;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

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

    public static final String TEST_MOVIE_WITHOUT_OSCARS_JSON = """
            {
                "id": 2,
                "name": "Test Movie Without Oscars",
                "coordinates": {
                    "x": 5,
                    "y": 50.0
                },
                "creationDate": "2024-01-01",
                "oscarsCount": 0,
                "goldenPalmCount": null,
                "budget": 500000.0,
                "genre": "FANTASY",
                "screenwriter": {
                    "name": "Test Writer 2",
                    "birthday": "1995-01-01",
                    "height": 1.8,
                    "weight": 75,
                    "passportID": "TEST456"
                }
            }
            """;

    public static final String TEST_MOVIES_ARRAY_JSON = """
            [
                {
                    "id": 1,
                    "name": "Test Movie 1",
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
                        "name": "Test Writer 1",
                        "birthday": "2000-01-01",
                        "height": 1.7,
                        "weight": 70,
                        "passportID": "TEST123"
                    }
                },
                {
                    "id": 2,
                    "name": "Test Movie 2",
                    "coordinates": {
                        "x": 5,
                        "y": 50.0
                    },
                    "creationDate": "2024-01-01",
                    "oscarsCount": 2,
                    "goldenPalmCount": null,
                    "budget": 500000.0,
                    "genre": "FANTASY",
                    "screenwriter": {
                        "name": "Test Writer 2",
                        "birthday": "1995-01-01",
                        "height": 1.8,
                        "weight": 75,
                        "passportID": "TEST456"
                    }
                }
            ]
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

    // Additional test data for comprehensive testing
    public static final String MOVIES_WITH_OSCAR_LOSERS_JSON = """
            [
                {
                    "id": 1,
                    "name": "Movie Without Oscars 1",
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
                        "name": "Loser Writer 1",
                        "birthday": "2000-01-01",
                        "height": 1.7,
                        "weight": 70,
                        "passportID": "LOSER123"
                    }
                },
                {
                    "id": 2,
                    "name": "Movie Without Oscars 2",
                    "coordinates": {
                        "x": 5,
                        "y": 50.0
                    },
                    "creationDate": "2024-01-01",
                    "oscarsCount": null,
                    "goldenPalmCount": null,
                    "budget": 500000.0,
                    "genre": "FANTASY",
                    "screenwriter": {
                        "name": "Loser Writer 2",
                        "birthday": "1995-01-01",
                        "height": 1.8,
                        "weight": 75,
                        "passportID": "LOSER456"
                    }
                },
                {
                    "id": 3,
                    "name": "Movie With Oscars",
                    "coordinates": {
                        "x": 15,
                        "y": 200.0
                    },
                    "creationDate": "2024-01-01",
                    "oscarsCount": 2,
                    "goldenPalmCount": 1,
                    "budget": 2000000.0,
                    "genre": "TRAGEDY",
                    "screenwriter": {
                        "name": "Winner Writer",
                        "birthday": "1985-01-01",
                        "height": 1.9,
                        "weight": 80,
                        "passportID": "WINNER123"
                    }
                }
            ]
            """;

    public static final String MOVIES_FOR_HONOR_BY_LENGTH_JSON = """
            [
                {
                    "id": 1,
                    "name": "Long Movie 1",
                    "coordinates": {
                        "x": 100,
                        "y": 200.0
                    },
                    "creationDate": "2024-01-01",
                    "oscarsCount": 1,
                    "goldenPalmCount": 1,
                    "budget": 1000000.50,
                    "genre": "ACTION",
                    "screenwriter": {
                        "name": "Writer 1",
                        "birthday": "2000-01-01",
                        "height": 1.7,
                        "weight": 70,
                        "passportID": "WRITER123"
                    }
                },
                {
                    "id": 2,
                    "name": "Short Movie",
                    "coordinates": {
                        "x": 10,
                        "y": 20.0
                    },
                    "creationDate": "2024-01-01",
                    "oscarsCount": 0,
                    "goldenPalmCount": null,
                    "budget": 500000.0,
                    "genre": "FANTASY",
                    "screenwriter": {
                        "name": "Writer 2",
                        "birthday": "1995-01-01",
                        "height": 1.8,
                        "weight": 75,
                        "passportID": "WRITER456"
                    }
                }
            ]
            """;

    public static final String MOVIES_FOR_HONOR_LOW_OSCARS_JSON = """
            [
                {
                    "id": 1,
                    "name": "Low Oscars Movie 1",
                    "coordinates": {
                        "x": 10,
                        "y": 120.5
                    },
                    "creationDate": "2024-01-01",
                    "oscarsCount": 1,
                    "goldenPalmCount": 1,
                    "budget": 1000000.50,
                    "genre": "ACTION",
                    "screenwriter": {
                        "name": "Writer 1",
                        "birthday": "2000-01-01",
                        "height": 1.7,
                        "weight": 70,
                        "passportID": "WRITER123"
                    }
                },
                {
                    "id": 2,
                    "name": "Low Oscars Movie 2",
                    "coordinates": {
                        "x": 5,
                        "y": 50.0
                    },
                    "creationDate": "2024-01-01",
                    "oscarsCount": 0,
                    "goldenPalmCount": null,
                    "budget": 500000.0,
                    "genre": "FANTASY",
                    "screenwriter": {
                        "name": "Writer 2",
                        "birthday": "1995-01-01",
                        "height": 1.8,
                        "weight": 75,
                        "passportID": "WRITER456"
                    }
                },
                {
                    "id": 3,
                    "name": "High Oscars Movie",
                    "coordinates": {
                        "x": 15,
                        "y": 200.0
                    },
                    "creationDate": "2024-01-01",
                    "oscarsCount": 5,
                    "goldenPalmCount": 2,
                    "budget": 2000000.0,
                    "genre": "TRAGEDY",
                    "screenwriter": {
                        "name": "Writer 3",
                        "birthday": "1985-01-01",
                        "height": 1.9,
                        "weight": 80,
                        "passportID": "WRITER789"
                    }
                }
            ]
            """;

    public static final String UPDATED_MOVIE_WITH_OSCARS_JSON = """
            {
                "id": 1,
                "name": "Test Movie",
                "coordinates": {
                    "x": 10,
                    "y": 120.5
                },
                "creationDate": "2024-01-01",
                "oscarsCount": 4,
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

    public static final String ERROR_RESPONSE_JSON = """
            {
                "message": "Validation error"
            }
            """;

    public static final String CALLBACK_REQUEST_BODY = """
            {
                "callbackUrl": "http://localhost:9090/callback"
            }
            """;

    // New test data for comprehensive testing

    // Test data for oscar losers scenarios
    public static final String EMPTY_MOVIES_ARRAY_JSON = "[]";

    public static final String MOVIES_WITH_ALL_WINNERS_JSON = """
            [
                {
                    "id": 1,
                    "name": "Winner Movie 1",
                    "coordinates": {
                        "x": 10,
                        "y": 120.5
                    },
                    "creationDate": "2024-01-01",
                    "oscarsCount": 2,
                    "goldenPalmCount": 1,
                    "budget": 1000000.50,
                    "genre": "ACTION",
                    "screenwriter": {
                        "name": "Winner Writer 1",
                        "birthday": "2000-01-01",
                        "height": 1.7,
                        "weight": 70,
                        "passportID": "WINNER123"
                    }
                },
                {
                    "id": 2,
                    "name": "Winner Movie 2",
                    "coordinates": {
                        "x": 5,
                        "y": 50.0
                    },
                    "creationDate": "2024-01-01",
                    "oscarsCount": 3,
                    "goldenPalmCount": 2,
                    "budget": 500000.0,
                    "genre": "FANTASY",
                    "screenwriter": {
                        "name": "Winner Writer 2",
                        "birthday": "1995-01-01",
                        "height": 1.8,
                        "weight": 75,
                        "passportID": "WINNER456"
                    }
                }
            ]
            """;

    // Test data for honor by length scenarios
    public static final String MOVIES_FOR_HONOR_BY_LENGTH_NO_MATCH_JSON = """
            [
                {
                    "id": 1,
                    "name": "Short Movie 1",
                    "coordinates": {
                        "x": 10,
                        "y": 20.0
                    },
                    "creationDate": "2024-01-01",
                    "oscarsCount": 1,
                    "goldenPalmCount": 1,
                    "budget": 1000000.50,
                    "genre": "ACTION",
                    "screenwriter": {
                        "name": "Writer 1",
                        "birthday": "2000-01-01",
                        "height": 1.7,
                        "weight": 70,
                        "passportID": "WRITER123"
                    }
                },
                {
                    "id": 2,
                    "name": "Short Movie 2",
                    "coordinates": {
                        "x": 5,
                        "y": 10.0
                    },
                    "creationDate": "2024-01-01",
                    "oscarsCount": 0,
                    "goldenPalmCount": null,
                    "budget": 500000.0,
                    "genre": "FANTASY",
                    "screenwriter": {
                        "name": "Writer 2",
                        "birthday": "1995-01-01",
                        "height": 1.8,
                        "weight": 75,
                        "passportID": "WRITER456"
                    }
                }
            ]
            """;

    public static final String MOVIES_FOR_HONOR_BY_LENGTH_MULTIPLE_JSON = """
            [
                {
                    "id": 1,
                    "name": "Long Movie 1",
                    "coordinates": {
                        "x": 100,
                        "y": 200.0
                    },
                    "creationDate": "2024-01-01",
                    "oscarsCount": 1,
                    "goldenPalmCount": 1,
                    "budget": 1000000.50,
                    "genre": "ACTION",
                    "screenwriter": {
                        "name": "Writer 1",
                        "birthday": "2000-01-01",
                        "height": 1.7,
                        "weight": 70,
                        "passportID": "WRITER123"
                    }
                },
                {
                    "id": 2,
                    "name": "Long Movie 2",
                    "coordinates": {
                        "x": 150,
                        "y": 250.0
                    },
                    "creationDate": "2024-01-01",
                    "oscarsCount": 0,
                    "goldenPalmCount": null,
                    "budget": 500000.0,
                    "genre": "FANTASY",
                    "screenwriter": {
                        "name": "Writer 2",
                        "birthday": "1995-01-01",
                        "height": 1.8,
                        "weight": 75,
                        "passportID": "WRITER456"
                    }
                },
                {
                    "id": 3,
                    "name": "Short Movie",
                    "coordinates": {
                        "x": 10,
                        "y": 20.0
                    },
                    "creationDate": "2024-01-01",
                    "oscarsCount": 2,
                    "goldenPalmCount": 1,
                    "budget": 2000000.0,
                    "genre": "TRAGEDY",
                    "screenwriter": {
                        "name": "Writer 3",
                        "birthday": "1985-01-01",
                        "height": 1.9,
                        "weight": 80,
                        "passportID": "WRITER789"
                    }
                }
            ]
            """;

    // Test data for honor low oscars scenarios
    public static final String MOVIES_FOR_HONOR_LOW_OSCARS_NO_MATCH_JSON = """
            [
                {
                    "id": 1,
                    "name": "High Oscars Movie 1",
                    "coordinates": {
                        "x": 10,
                        "y": 120.5
                    },
                    "creationDate": "2024-01-01",
                    "oscarsCount": 5,
                    "goldenPalmCount": 1,
                    "budget": 1000000.50,
                    "genre": "ACTION",
                    "screenwriter": {
                        "name": "Writer 1",
                        "birthday": "2000-01-01",
                        "height": 1.7,
                        "weight": 70,
                        "passportID": "WRITER123"
                    }
                },
                {
                    "id": 2,
                    "name": "High Oscars Movie 2",
                    "coordinates": {
                        "x": 5,
                        "y": 50.0
                    },
                    "creationDate": "2024-01-01",
                    "oscarsCount": 4,
                    "goldenPalmCount": 2,
                    "budget": 500000.0,
                    "genre": "FANTASY",
                    "screenwriter": {
                        "name": "Writer 2",
                        "birthday": "1995-01-01",
                        "height": 1.8,
                        "weight": 75,
                        "passportID": "WRITER456"
                    }
                }
            ]
            """;

    public static final String MOVIES_FOR_HONOR_LOW_OSCARS_MULTIPLE_JSON = """
            [
                {
                    "id": 1,
                    "name": "Low Oscars Movie 1",
                    "coordinates": {
                        "x": 10,
                        "y": 120.5
                    },
                    "creationDate": "2024-01-01",
                    "oscarsCount": 1,
                    "goldenPalmCount": 1,
                    "budget": 1000000.50,
                    "genre": "ACTION",
                    "screenwriter": {
                        "name": "Writer 1",
                        "birthday": "2000-01-01",
                        "height": 1.7,
                        "weight": 70,
                        "passportID": "WRITER123"
                    }
                },
                {
                    "id": 2,
                    "name": "Low Oscars Movie 2",
                    "coordinates": {
                        "x": 5,
                        "y": 50.0
                    },
                    "creationDate": "2024-01-01",
                    "oscarsCount": 0,
                    "goldenPalmCount": null,
                    "budget": 500000.0,
                    "genre": "FANTASY",
                    "screenwriter": {
                        "name": "Writer 2",
                        "birthday": "1995-01-01",
                        "height": 1.8,
                        "weight": 75,
                        "passportID": "WRITER456"
                    }
                },
                {
                    "id": 3,
                    "name": "High Oscars Movie",
                    "coordinates": {
                        "x": 15,
                        "y": 200.0
                    },
                    "creationDate": "2024-01-01",
                    "oscarsCount": 5,
                    "goldenPalmCount": 2,
                    "budget": 2000000.0,
                    "genre": "TRAGEDY",
                    "screenwriter": {
                        "name": "Writer 3",
                        "birthday": "1985-01-01",
                        "height": 1.9,
                        "weight": 80,
                        "passportID": "WRITER789"
                    }
                }
            ]
            """;

    // Test data for get oscars by movie scenarios
    public static final String MOVIE_WITH_OSCARS_JSON = """
            {
                "id": 1,
                "name": "Movie With Oscars",
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

    public static final String MOVIE_WITHOUT_OSCARS_JSON = """
            {
                "id": 2,
                "name": "Movie Without Oscars",
                "coordinates": {
                    "x": 5,
                    "y": 50.0
                },
                "creationDate": "2024-01-01",
                "oscarsCount": 0,
                "goldenPalmCount": null,
                "budget": 500000.0,
                "genre": "FANTASY",
                "screenwriter": {
                    "name": "Test Writer 2",
                    "birthday": "1995-01-01",
                    "height": 1.8,
                    "weight": 75,
                    "passportID": "TEST456"
                }
            }
            """;

    public static final String MOVIE_WITH_NULL_OSCARS_JSON = """
            {
                "id": 3,
                "name": "Movie With Null Oscars",
                "coordinates": {
                    "x": 15,
                    "y": 200.0
                },
                "creationDate": "2024-01-01",
                "oscarsCount": null,
                "goldenPalmCount": 1,
                "budget": 2000000.0,
                "genre": "TRAGEDY",
                "screenwriter": {
                    "name": "Test Writer 3",
                    "birthday": "1985-01-01",
                    "height": 1.9,
                    "weight": 80,
                    "passportID": "TEST789"
                }
            }
            """;

    // Test data for add oscars scenarios
    public static final String MOVIE_FOR_ADD_OSCARS_JSON = """
            {
                "id": 1,
                "name": "Movie For Add Oscars",
                "coordinates": {
                    "x": 10,
                    "y": 120.5
                },
                "creationDate": "2024-01-01",
                "oscarsCount": 2,
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

    public static final String MOVIE_FOR_ADD_OSCARS_WITH_NULL_JSON = """
            {
                "id": 2,
                "name": "Movie For Add Oscars With Null",
                "coordinates": {
                    "x": 5,
                    "y": 50.0
                },
                "creationDate": "2024-01-01",
                "oscarsCount": null,
                "goldenPalmCount": null,
                "budget": 500000.0,
                "genre": "FANTASY",
                "screenwriter": {
                    "name": "Test Writer 2",
                    "birthday": "1995-01-01",
                    "height": 1.8,
                    "weight": 75,
                    "passportID": "TEST456"
                }
            }
            """;

    public static final String UPDATED_MOVIE_AFTER_ADD_OSCARS_JSON = """
            {
                "id": 1,
                "name": "Movie For Add Oscars",
                "coordinates": {
                    "x": 10,
                    "y": 120.5
                },
                "creationDate": "2024-01-01",
                "oscarsCount": 5,
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

    public static final String UPDATED_MOVIE_AFTER_ADD_OSCARS_WITH_NULL_JSON = """
            {
                "id": 2,
                "name": "Movie For Add Oscars With Null",
                "coordinates": {
                    "x": 5,
                    "y": 50.0
                },
                "creationDate": "2024-01-01",
                "oscarsCount": 3,
                "goldenPalmCount": null,
                "budget": 500000.0,
                "genre": "FANTASY",
                "screenwriter": {
                    "name": "Test Writer 2",
                    "birthday": "1995-01-01",
                    "height": 1.8,
                    "weight": 75,
                    "passportID": "TEST456"
                }
            }
            """;

    // Test data for delete oscars scenarios
    public static final String MOVIE_FOR_DELETE_OSCARS_JSON = """
            {
                "id": 1,
                "name": "Movie For Delete Oscars",
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

    public static final String UPDATED_MOVIE_AFTER_DELETE_OSCARS_JSON = """
            {
                "id": 1,
                "name": "Movie For Delete Oscars",
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

    // Error scenarios
    public static final String MOVIES_SERVICE_ERROR_JSON = """
            {
                "message": "Internal server error"
            }
            """;

    public static final String MOVIES_SERVICE_NOT_FOUND_JSON = """
            {
                "message": "Movie not found"
            }
            """;

    // Callback test data
    public static final String VALID_CALLBACK_URL = "http://localhost:9090/callback";
    public static final String INVALID_CALLBACK_URL = "invalid-url";
    public static final String EMPTY_CALLBACK_URL = "";

    // Helper methods for creating test objects
    public static Movie createMovieWithOscars(Long id, String name, int oscarsCount) {
        return new Movie(
                id,
                name,
                new Coordinates(10, 120.5),
                LocalDate.of(2024, 1, 1),
                oscarsCount,
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

    public static Movie createMovieWithoutOscars(Long id, String name) {
        return new Movie(
                id,
                name,
                new Coordinates(5, 50.0),
                LocalDate.of(2024, 1, 1),
                0,
                null,
                BigDecimal.valueOf(500000.0),
                MovieGenre.FANTASY,
                new Person(
                        "Test Writer 2",
                        LocalDate.of(1995, 1, 1),
                        1.8,
                        75,
                        "TEST456"
                )
        );
    }

    public static Person createPerson(String name, String passportId) {
        return new Person(
                name,
                LocalDate.of(1990, 5, 15),
                1.75,
                65,
                passportId
        );
    }

    // Helper methods for returning Java objects instead of JSON strings

    public static Movie createMovieWithCoordinates(Long id, String name, double x, double y, int oscarsCount) {
        return new Movie(
                id,
                name,
                new Coordinates((int) x, y),
                LocalDate.of(2024, 1, 1),
                oscarsCount,
                1,
                BigDecimal.valueOf(1000000.50),
                MovieGenre.ACTION,
                new Person("Test Writer", LocalDate.of(2000, 1, 1), 1.7, 70, "TEST123")
        );
    }

    // Helper methods for returning Java objects instead of JSON strings
    public static List<Movie> getMoviesWithOscarLosers() {
        return List.of(
                new Movie(
                        1L,
                        "Movie Without Oscars 1",
                        new Coordinates(10, 120.5),
                        LocalDate.of(2024, 1, 1),
                        0,
                        1,
                        BigDecimal.valueOf(1000000.50),
                        MovieGenre.ACTION,
                        new Person("Loser Writer 1", LocalDate.of(2000, 1, 1), 1.7, 70, "LOSER123")
                ),
                new Movie(
                        2L,
                        "Movie Without Oscars 2",
                        new Coordinates(5, 50.0),
                        LocalDate.of(2024, 1, 1),
                        null,
                        null,
                        BigDecimal.valueOf(500000.0),
                        MovieGenre.FANTASY,
                        new Person("Loser Writer 2", LocalDate.of(1995, 1, 1), 1.8, 75, "LOSER456")
                ),
                new Movie(
                        3L,
                        "Movie With Oscars",
                        new Coordinates(15, 200.0),
                        LocalDate.of(2024, 1, 1),
                        2,
                        1,
                        BigDecimal.valueOf(2000000.0),
                        MovieGenre.TRAGEDY,
                        new Person("Winner Writer", LocalDate.of(1985, 1, 1), 1.9, 80, "WINNER123")
                )
        );
    }

    public static List<Movie> getMoviesWithAllWinners() {
        return List.of(
                createMovieWithOscars(1L, "Winner Movie 1", 2),
                createMovieWithOscars(2L, "Winner Movie 2", 3)
        );
    }

    public static List<Movie> getMoviesForHonorByLength() {
        return List.of(
                createMovieWithCoordinates(1L, "Long Movie 1", 100.0, 200.0, 1),
                createMovieWithCoordinates(2L, "Short Movie", 10.0, 20.0, 0)
        );
    }

    public static List<Movie> getMoviesForHonorByLengthMultiple() {
        return List.of(
                createMovieWithCoordinates(1L, "Long Movie 1", 100.0, 200.0, 1),
                createMovieWithCoordinates(2L, "Long Movie 2", 150.0, 250.0, 0),
                createMovieWithCoordinates(3L, "Short Movie", 10.0, 20.0, 2)
        );
    }

    public static List<Movie> getMoviesForHonorByLengthNoMatch() {
        return List.of(
                createMovieWithCoordinates(1L, "Short Movie 1", 10.0, 20.0, 1),
                createMovieWithCoordinates(2L, "Short Movie 2", 5.0, 10.0, 0)
        );
    }

    public static List<Movie> getMoviesForHonorLowOscars() {
        return List.of(
                createMovieWithOscars(1L, "Low Oscars Movie 1", 1),
                createMovieWithOscars(2L, "Low Oscars Movie 2", 0),
                createMovieWithOscars(3L, "High Oscars Movie", 5)
        );
    }

    public static List<Movie> getMoviesForHonorLowOscarsMultiple() {
        return List.of(
                createMovieWithOscars(1L, "Low Oscars Movie 1", 1),
                createMovieWithOscars(2L, "Low Oscars Movie 2", 0),
                createMovieWithOscars(3L, "High Oscars Movie", 5)
        );
    }

    public static List<Movie> getMoviesForHonorLowOscarsNoMatch() {
        return List.of(
                createMovieWithOscars(1L, "High Oscars Movie 1", 5),
                createMovieWithOscars(2L, "High Oscars Movie 2", 4)
        );
    }

    public static Movie getMovieWithOscars() {
        return createMovieWithOscars(1L, "Movie With Oscars", 3);
    }

    public static Movie getMovieWithoutOscars() {
        return createMovieWithoutOscars(2L, "Movie Without Oscars");
    }

    public static Movie getMovieWithNullOscars() {
        return new Movie(
                3L,
                "Movie With Null Oscars",
                new Coordinates(15, 200.0),
                LocalDate.of(2024, 1, 1),
                null,
                1,
                BigDecimal.valueOf(2000000.0),
                MovieGenre.TRAGEDY,
                new Person("Test Writer 3", LocalDate.of(1985, 1, 1), 1.9, 80, "TEST789")
        );
    }

    public static Movie getMovieForAddOscars() {
        return createMovieWithOscars(1L, "Movie For Add Oscars", 2);
    }

    public static Movie getMovieForAddOscarsWithNull() {
        return new Movie(
                2L,
                "Movie For Add Oscars With Null",
                new Coordinates(5, 50.0),
                LocalDate.of(2024, 1, 1),
                null,
                null,
                BigDecimal.valueOf(500000.0),
                MovieGenre.FANTASY,
                new Person("Test Writer 2", LocalDate.of(1995, 1, 1), 1.8, 75, "TEST456")
        );
    }

    public static Movie getUpdatedMovieAfterAddOscars() {
        return createMovieWithOscars(1L, "Movie For Add Oscars", 5);
    }

    public static Movie getUpdatedMovieAfterAddOscarsWithNull() {
        return createMovieWithOscars(2L, "Movie For Add Oscars With Null", 3);
    }

    public static Movie getMovieForDeleteOscars() {
        return createMovieWithOscars(1L, "Movie For Delete Oscars", 3);
    }

    public static Movie getUpdatedMovieAfterDeleteOscars() {
        return createMovieWithOscars(1L, "Movie For Delete Oscars", 0);
    }

    public static Movie getUpdatedMovieWithOscars() {
        return createMovieWithOscars(1L, "Test Movie", 4);
    }
}
