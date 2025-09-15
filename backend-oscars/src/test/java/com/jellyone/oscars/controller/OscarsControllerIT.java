package com.jellyone.oscars.controller;

import com.jellyone.oscars.testdata.TestDataProvider;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static io.restassured.RestAssured.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class OscarsControllerIT {

    @LocalServerPort
    private int port;

    static MockWebServer movies;

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry r) {
        try {
            if (movies == null) {
                movies = new MockWebServer();
                movies.start();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        r.add("movies.api.base-url", () -> movies.url("/").toString().replaceAll("/$", ""));
    }

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost:" + port;
        // No global reset; tests enqueue specific responses
    }

    @AfterAll
    static void tearDownAll() {
        try {
            if (movies != null) movies.shutdown();
        } catch (Exception ignored) {}
    }

    @Test
    void getOscarLosers_noContent() {
        given()
                .when()
                .get("/oscars/operators/losers")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }


    @Test
    void addOscars_ok_whenMovieExists() {
        // Мок для GET запроса (получение фильма)
        movies.enqueue(new MockResponse().setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody(TestDataProvider.TEST_MOVIE_JSON));
        
        // Мок для PATCH запроса (обновление фильма)
        movies.enqueue(new MockResponse().setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody(TestDataProvider.TEST_MOVIE_WITH_OSCARS_JSON));

        given()
                .queryParam("oscarsToAdd", 1)
                .when()
                .post("/oscars/movies/{movieId}", 1)
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON);
    }

    @Test
    void honorByLength_ok() {
        // Мок для GET запроса к /movies (получение списка фильмов)
        movies.enqueue(new MockResponse().setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody("[]")); // Пустой список фильмов

        given()
                .queryParam("oscarsToAdd", 2)
                .when()
                .post("/oscars/movies/honor-by-length/{minLength}", 120)
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON);
    }

    @Test
    void honorLowOscars_ok() {
        // Мок для GET запроса к /movies (получение списка фильмов)
        movies.enqueue(new MockResponse().setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody("[]")); // Пустой список фильмов

        given()
                .queryParam("maxOscars", 1)
                .queryParam("oscarsToAdd", 1)
                .when()
                .post("/oscars/movies/honor-low-oscars")
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON);
    }

    @Test
    void deleteOscarsByMovie_notModified_whenNoAwards() {
        // Мок для GET запроса (получение фильма)
        movies.enqueue(new MockResponse().setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody(TestDataProvider.TEST_MOVIE_JSON));
        
        // Мок для PATCH запроса (обнуление оскаров)
        movies.enqueue(new MockResponse().setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody(TestDataProvider.TEST_MOVIE_JSON));

        given()
                .when()
                .delete("/oscars/movies/{movieId}", 1)
                .then()
                .statusCode(304);
    }
}


