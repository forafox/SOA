package com.jellyone.oscars.controller;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import io.restassured.RestAssured;
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
class BasicOscarsControllerTest {

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
    }

    @AfterAll
    static void tearDownAll() {
        try {
            if (movies != null) movies.shutdown();
        } catch (Exception ignored) {}
    }

    @Test
    void getOscarLosers_noContent_whenNoMovies() {
        // Мок для GET запроса к /movies (пустой список фильмов)
        movies.enqueue(new MockResponse().setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody("[]"));

        given()
                .when()
                .get("/oscars/operators/losers")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void getOscarsByMovie_noContent_whenMovieNotFound() {
        // Мок-сервер возвращает 404 для несуществующего фильма
        movies.enqueue(new MockResponse().setResponseCode(404)
                .setHeader("Content-Type", "application/json")
                .setBody("{\"message\": \"Movie not found\"}"));

        given()
                .when()
                .get("/oscars/movies/{movieId}", 999)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void addOscars_ok_whenMovieExists() {
        // Мок для GET запроса (получение фильма)
        movies.enqueue(new MockResponse().setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody("{\"id\":1,\"name\":\"Test Movie\",\"oscarsCount\":0}"));
        
        // Мок для PATCH запроса (обновление фильма)
        movies.enqueue(new MockResponse().setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody("{\"id\":1,\"name\":\"Test Movie\",\"oscarsCount\":1}"));

        given()
                .queryParam("oscarsToAdd", 1)
                .when()
                .post("/oscars/movies/{movieId}", 1)
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    void honorByLength_ok_whenNoMovies() {
        // Мок для GET запроса к /movies (пустой список фильмов)
        movies.enqueue(new MockResponse().setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody("[]"));

        given()
                .queryParam("oscarsToAdd", 2)
                .when()
                .post("/oscars/movies/honor-by-length/{minLength}", 120)
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    void honorLowOscars_ok_whenNoMovies() {
        // Мок для GET запроса к /movies (пустой список фильмов)
        movies.enqueue(new MockResponse().setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody("[]"));

        given()
                .queryParam("maxOscars", 1)
                .queryParam("oscarsToAdd", 1)
                .when()
                .post("/oscars/movies/honor-low-oscars")
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    void deleteOscarsByMovie_notModified_whenNoAwards() {
        // Мок для GET запроса (получение фильма без Оскаров)
        movies.enqueue(new MockResponse().setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody("{\"id\":2,\"name\":\"Test Movie\",\"oscarsCount\":0}"));

        given()
                .when()
                .delete("/oscars/movies/{movieId}", 2)
                .then()
                .statusCode(304);
    }
}
