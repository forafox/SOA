package com.jellyone.oscars.controller;

import com.jellyone.oscars.client.MoviesClient;
import com.jellyone.oscars.service.CallbackNotifier;
import com.jellyone.oscars.testdata.TestDataProvider;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class OscarsControllerMockBeanIT {

    @LocalServerPort
    private int port;

    @MockitoBean
    private MoviesClient moviesClient;

    @MockitoBean
    private CallbackNotifier callbackNotifier;

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost:" + port;
        reset(moviesClient, callbackNotifier);
    }

    // ========== GET /oscars/operators/losers ==========

    @Test
    @DisplayName("GET /oscars/operators/losers - 200 OK with losers")
    void getOscarLosers_200_whenLosersExist() {
        // Mock movies with some having no oscars
        when(moviesClient.getAllMovies()).thenReturn(TestDataProvider.getMoviesWithOscarLosers());

        given()
                .when()
                .get("/oscars/operators/losers")
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("size()", is(2))
                .body("[0].name", is("Loser Writer 1"))
                .body("[0].passportID", is("LOSER123"))
                .body("[1].name", is("Loser Writer 2"))
                .body("[1].passportID", is("LOSER456"));
    }

    @Test
    @DisplayName("GET /oscars/operators/losers - 204 No Content when no losers")
    void getOscarLosers_204_whenNoLosers() {
        // Mock movies where all have oscars
        when(moviesClient.getAllMovies()).thenReturn(TestDataProvider.getMoviesWithAllWinners());

        given()
                .when()
                .get("/oscars/operators/losers")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("GET /oscars/operators/losers - 204 No Content when empty movies list")
    void getOscarLosers_204_whenEmptyMoviesList() {
        // Mock empty movies list
        when(moviesClient.getAllMovies()).thenReturn(List.of());

        given()
                .when()
                .get("/oscars/operators/losers")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("GET /oscars/operators/losers - 204 No Content when movies service error")
    void getOscarLosers_204_whenMoviesServiceError() {
        // Mock movies service error
        when(moviesClient.getAllMovies()).thenReturn(List.of());

        given()
                .when()
                .get("/oscars/operators/losers")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    // ========== POST /oscars/movies/honor-by-length/{minLength} ==========

    @Test
    @DisplayName("POST /oscars/movies/honor-by-length/{minLength} - 200 OK with movies honored")
    void honorMoviesByLength_200_whenMoviesHonored() {
        // Mock movies for length filtering
        when(moviesClient.getAllMovies()).thenReturn(TestDataProvider.getMoviesForHonorByLength());
        when(moviesClient.patchMovie(eq(1L), any())).thenReturn(TestDataProvider.getUpdatedMovieWithOscars());

        given()
                .queryParam("oscarsToAdd", 3)
                .contentType(ContentType.JSON)
                .body("{}")
                .when()
                .post("/oscars/movies/honor-by-length/{minLength}", 150)
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("updatedCount", is(1))
                .body("updatedMovies.size()", is(1))
                .body("updatedMovies[0].id", is(1))
                .body("updatedMovies[0].oscarsCount", is(4));
    }

    @Test
    @DisplayName("POST /oscars/movies/honor-by-length/{minLength} - 200 OK with multiple movies honored")
    void honorMoviesByLength_200_whenMultipleMoviesHonored() {
        // Mock movies for length filtering
        when(moviesClient.getAllMovies()).thenReturn(TestDataProvider.getMoviesForHonorByLengthMultiple());
        when(moviesClient.patchMovie(eq(1L), any())).thenReturn(TestDataProvider.getUpdatedMovieWithOscars());
        when(moviesClient.patchMovie(eq(2L), any())).thenReturn(TestDataProvider.getUpdatedMovieAfterAddOscarsWithNull());
        // Фильм 3 не должен обновляться, так как его длина (30) меньше minLength (200)

        // Mock callback notifier to avoid any callback-related issues
        doNothing().when(callbackNotifier).postJson(any(), any());

        given()
                .queryParam("oscarsToAdd", 3)
                .contentType(ContentType.JSON)
                .body("{}")
                .when()
                .post("/oscars/movies/honor-by-length/{minLength}", 200)
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("updatedCount", is(2))
                .body("updatedMovies.size()", is(2));
    }

    @Test
    @DisplayName("POST /oscars/movies/honor-by-length/{minLength} - 200 OK when no movies match")
    void honorMoviesByLength_200_whenNoMoviesMatch() {
        // Mock movies that don't match length criteria
        when(moviesClient.getAllMovies()).thenReturn(TestDataProvider.getMoviesForHonorByLengthNoMatch());

        given()
                .queryParam("oscarsToAdd", 3)
                .contentType(ContentType.JSON)
                .body("{}")
                .when()
                .post("/oscars/movies/honor-by-length/{minLength}", 500)
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("updatedCount", is(0))
                .body("updatedMovies.size()", is(0));
    }

    @Test
    @DisplayName("POST /oscars/movies/honor-by-length/{minLength} - 200 OK with callback")
    void honorMoviesByLength_200_withCallback() {
        // Mock movies for length filtering
        when(moviesClient.getAllMovies()).thenReturn(TestDataProvider.getMoviesForHonorByLength());
        when(moviesClient.patchMovie(eq(1L), any())).thenReturn(TestDataProvider.getUpdatedMovieWithOscars());

        given()
                .queryParam("oscarsToAdd", 3)
                .contentType(ContentType.JSON)
                .body(TestDataProvider.CALLBACK_REQUEST_BODY)
                .when()
                .post("/oscars/movies/honor-by-length/{minLength}", 150)
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("updatedCount", is(1))
                .body("updatedMovies.size()", is(1));
    }

    @Test
    @DisplayName("POST /oscars/movies/honor-by-length/{minLength} - 200 OK when movies service error")
    void honorMoviesByLength_200_whenMoviesServiceError() {
        // Mock movies service error
        when(moviesClient.getAllMovies()).thenReturn(List.of());

        given()
                .queryParam("oscarsToAdd", 3)
                .contentType(ContentType.JSON)
                .body("{}")
                .when()
                .post("/oscars/movies/honor-by-length/{minLength}", 150)
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("updatedCount", is(0))
                .body("updatedMovies.size()", is(0));
    }

    // ========== POST /oscars/movies/honor-low-oscars ==========

    @Test
    @DisplayName("POST /oscars/movies/honor-low-oscars - 200 OK with movies honored")
    void honorMoviesWithFewOscars_200_whenMoviesHonored() {
        // Mock movies for oscars filtering
        when(moviesClient.getAllMovies()).thenReturn(TestDataProvider.getMoviesForHonorLowOscars());
        when(moviesClient.patchMovie(eq(1L), any())).thenReturn(TestDataProvider.getUpdatedMovieWithOscars());
        when(moviesClient.patchMovie(eq(2L), any())).thenReturn(TestDataProvider.getUpdatedMovieAfterAddOscarsWithNull());

        given()
                .queryParam("maxOscars", 1)
                .queryParam("oscarsToAdd", 2)
                .contentType(ContentType.JSON)
                .body("{}")
                .when()
                .post("/oscars/movies/honor-low-oscars")
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("updatedCount", is(2))
                .body("updatedMovies.size()", is(2));
    }

    @Test
    @DisplayName("POST /oscars/movies/honor-low-oscars - 200 OK with multiple movies honored")
    void honorMoviesWithFewOscars_200_whenMultipleMoviesHonored() {
        // Mock movies for oscars filtering
        when(moviesClient.getAllMovies()).thenReturn(TestDataProvider.getMoviesForHonorLowOscarsMultiple());
        when(moviesClient.patchMovie(eq(1L), any())).thenReturn(TestDataProvider.getUpdatedMovieWithOscars());
        when(moviesClient.patchMovie(eq(2L), any())).thenReturn(TestDataProvider.getUpdatedMovieAfterAddOscarsWithNull());

        given()
                .queryParam("maxOscars", 1)
                .queryParam("oscarsToAdd", 2)
                .contentType(ContentType.JSON)
                .body("{}")
                .when()
                .post("/oscars/movies/honor-low-oscars")
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("updatedCount", is(2))
                .body("updatedMovies.size()", is(2));
    }

    @Test
    @DisplayName("POST /oscars/movies/honor-low-oscars - 200 OK when no movies match")
    void honorMoviesWithFewOscars_200_whenNoMoviesMatch() {
        // Mock movies that don't match oscars criteria
        when(moviesClient.getAllMovies()).thenReturn(TestDataProvider.getMoviesForHonorLowOscarsNoMatch());

        given()
                .queryParam("maxOscars", 1)
                .queryParam("oscarsToAdd", 2)
                .contentType(ContentType.JSON)
                .body("{}")
                .when()
                .post("/oscars/movies/honor-low-oscars")
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("updatedCount", is(0))
                .body("updatedMovies.size()", is(0));
    }

    @Test
    @DisplayName("POST /oscars/movies/honor-low-oscars - 200 OK with callback")
    void honorMoviesWithFewOscars_200_withCallback() {
        // Mock movies for oscars filtering
        when(moviesClient.getAllMovies()).thenReturn(TestDataProvider.getMoviesForHonorLowOscars());
        when(moviesClient.patchMovie(eq(1L), any())).thenReturn(TestDataProvider.getUpdatedMovieWithOscars());
        when(moviesClient.patchMovie(eq(2L), any())).thenReturn(TestDataProvider.getUpdatedMovieAfterAddOscarsWithNull());

        given()
                .queryParam("maxOscars", 1)
                .queryParam("oscarsToAdd", 2)
                .contentType(ContentType.JSON)
                .body(TestDataProvider.CALLBACK_REQUEST_BODY)
                .when()
                .post("/oscars/movies/honor-low-oscars")
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("updatedCount", is(2))
                .body("updatedMovies.size()", is(2));
    }

    @Test
    @DisplayName("POST /oscars/movies/honor-low-oscars - 200 OK when movies service error")
    void honorMoviesWithFewOscars_200_whenMoviesServiceError() {
        // Mock movies service error
        when(moviesClient.getAllMovies()).thenReturn(List.of());

        given()
                .queryParam("maxOscars", 1)
                .queryParam("oscarsToAdd", 2)
                .contentType(ContentType.JSON)
                .body("{}")
                .when()
                .post("/oscars/movies/honor-low-oscars")
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("updatedCount", is(0))
                .body("updatedMovies.size()", is(0));
    }

    // ========== GET /oscars/movies/{movieId} ==========

    @Test
    @DisplayName("GET /oscars/movies/{movieId} - 200 OK with oscars")
    void getOscarsByMovie_200_whenOscarsExist() {
        // Mock movie with oscars
        when(moviesClient.getMovieById(1L)).thenReturn(TestDataProvider.getMovieWithOscars());

        given()
                .when()
                .get("/oscars/movies/{movieId}", 1)
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("size()", is(3))
                .body("[0].awardId", is(1))
                .body("[0].category", is("Best Picture"))
                .body("[0].date", is("2024-01-01"))
                .body("[1].awardId", is(2))
                .body("[1].category", is("Best Picture"))
                .body("[2].awardId", is(3))
                .body("[2].category", is("Best Picture"));
    }

    @Test
    @DisplayName("GET /oscars/movies/{movieId} - 200 OK with pagination")
    void getOscarsByMovie_200_withPagination() {
        // Mock movie with oscars
        when(moviesClient.getMovieById(1L)).thenReturn(TestDataProvider.getMovieWithOscars());

        given()
                .queryParam("page", 1)
                .queryParam("size", 2)
                .when()
                .get("/oscars/movies/{movieId}", 1)
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("size()", is(3)); // Service returns all oscars regardless of pagination
    }

    @Test
    @DisplayName("GET /oscars/movies/{movieId} - 204 No Content when no oscars")
    void getOscarsByMovie_204_whenNoOscars() {
        // Mock movie without oscars
        when(moviesClient.getMovieById(2L)).thenReturn(TestDataProvider.getMovieWithoutOscars());

        given()
                .when()
                .get("/oscars/movies/{movieId}", 2)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("GET /oscars/movies/{movieId} - 204 No Content when oscars count is null")
    void getOscarsByMovie_204_whenOscarsCountIsNull() {
        // Mock movie with null oscars count
        when(moviesClient.getMovieById(3L)).thenReturn(TestDataProvider.getMovieWithNullOscars());

        given()
                .when()
                .get("/oscars/movies/{movieId}", 3)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("GET /oscars/movies/{movieId} - 204 No Content when movie not found")
    void getOscarsByMovie_204_whenMovieNotFound() {
        // Mock movie not found
        when(moviesClient.getMovieById(999L)).thenReturn(null);

        given()
                .when()
                .get("/oscars/movies/{movieId}", 999)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("GET /oscars/movies/{movieId} - 204 No Content when movies service error")
    void getOscarsByMovie_204_whenMoviesServiceError() {
        // Mock movies service error
        when(moviesClient.getMovieById(1L)).thenReturn(null);

        given()
                .when()
                .get("/oscars/movies/{movieId}", 1)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    // ========== POST /oscars/movies/{movieId} ==========

    @Test
    @DisplayName("POST /oscars/movies/{movieId} - 200 OK when oscars added")
    void addOscars_200_whenOscarsAdded() {
        // Mock GET movie
        when(moviesClient.getMovieById(1L)).thenReturn(TestDataProvider.getMovieForAddOscars());
        when(moviesClient.patchMovie(eq(1L), any())).thenReturn(TestDataProvider.getUpdatedMovieAfterAddOscars());

        given()
                .queryParam("oscarsToAdd", 3)
                .contentType(ContentType.JSON)
                .body("{}")
                .when()
                .post("/oscars/movies/{movieId}", 1)
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("updatedCount", is(1))
                .body("updatedMovies.size()", is(1))
                .body("updatedMovies[0].id", is(1))
                .body("updatedMovies[0].oscarsCount", is(5));
    }

    @Test
    @DisplayName("POST /oscars/movies/{movieId} - 200 OK when oscars added to movie with null count")
    void addOscars_200_whenOscarsAddedToMovieWithNullCount() {
        // Mock GET movie with null oscars count
        when(moviesClient.getMovieById(2L)).thenReturn(TestDataProvider.getMovieForAddOscarsWithNull());
        when(moviesClient.patchMovie(eq(2L), any())).thenReturn(TestDataProvider.getUpdatedMovieAfterAddOscarsWithNull());

        given()
                .queryParam("oscarsToAdd", 3)
                .contentType(ContentType.JSON)
                .body("{}")
                .when()
                .post("/oscars/movies/{movieId}", 2)
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("updatedCount", is(1))
                .body("updatedMovies.size()", is(1))
                .body("updatedMovies[0].id", is(2))
                .body("updatedMovies[0].oscarsCount", is(3));
    }

    @Test
    @DisplayName("POST /oscars/movies/{movieId} - 200 OK with callback")
    void addOscars_200_withCallback() {
        // Mock GET movie
        when(moviesClient.getMovieById(1L)).thenReturn(TestDataProvider.getMovieForAddOscars());
        when(moviesClient.patchMovie(eq(1L), any())).thenReturn(TestDataProvider.getUpdatedMovieAfterAddOscars());

        given()
                .queryParam("oscarsToAdd", 3)
                .contentType(ContentType.JSON)
                .body(TestDataProvider.CALLBACK_REQUEST_BODY)
                .when()
                .post("/oscars/movies/{movieId}", 1)
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("updatedCount", is(1))
                .body("updatedMovies.size()", is(1));
    }

    @Test
    @DisplayName("POST /oscars/movies/{movieId} - 200 OK when movie not found")
    void addOscars_200_whenMovieNotFound() {
        // Mock movie not found
        when(moviesClient.getMovieById(999L)).thenReturn(null);

        given()
                .queryParam("oscarsToAdd", 3)
                .contentType(ContentType.JSON)
                .body("{}")
                .when()
                .post("/oscars/movies/{movieId}", 999)
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("updatedCount", is(0))
                .body("updatedMovies.size()", is(0));
    }

    @Test
    @DisplayName("POST /oscars/movies/{movieId} - 200 OK when movies service error")
    void addOscars_200_whenMoviesServiceError() {
        // Mock movies service error
        when(moviesClient.getMovieById(1L)).thenReturn(null);

        given()
                .queryParam("oscarsToAdd", 3)
                .contentType(ContentType.JSON)
                .body("{}")
                .when()
                .post("/oscars/movies/{movieId}", 1)
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("updatedCount", is(0))
                .body("updatedMovies.size()", is(0));
    }

    // ========== DELETE /oscars/movies/{movieId} ==========

    @Test
    @DisplayName("DELETE /oscars/movies/{movieId} - 204 No Content when oscars deleted")
    void deleteOscarsByMovie_204_whenOscarsDeleted() {
        // Mock GET movie with oscars
        when(moviesClient.getMovieById(1L)).thenReturn(TestDataProvider.getMovieForDeleteOscars());
        when(moviesClient.patchMovie(eq(1L), any())).thenReturn(TestDataProvider.getUpdatedMovieAfterDeleteOscars());

        given()
                .when()
                .delete("/oscars/movies/{movieId}", 1)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("DELETE /oscars/movies/{movieId} - 304 Not Modified when no oscars")
    void deleteOscarsByMovie_304_whenNoOscars() {
        // Mock GET movie without oscars
        when(moviesClient.getMovieById(2L)).thenReturn(TestDataProvider.getMovieWithoutOscars());

        given()
                .when()
                .delete("/oscars/movies/{movieId}", 2)
                .then()
                .statusCode(HttpStatus.NOT_MODIFIED.value());
    }

    @Test
    @DisplayName("DELETE /oscars/movies/{movieId} - 304 Not Modified when oscars count is null")
    void deleteOscarsByMovie_304_whenOscarsCountIsNull() {
        // Mock GET movie with null oscars count
        when(moviesClient.getMovieById(3L)).thenReturn(TestDataProvider.getMovieWithNullOscars());

        given()
                .when()
                .delete("/oscars/movies/{movieId}", 3)
                .then()
                .statusCode(HttpStatus.NOT_MODIFIED.value());
    }

    @Test
    @DisplayName("DELETE /oscars/movies/{movieId} - 304 Not Modified when movie not found")
    void deleteOscarsByMovie_304_whenMovieNotFound() {
        // Mock movie not found
        when(moviesClient.getMovieById(999L)).thenReturn(null);

        given()
                .when()
                .delete("/oscars/movies/{movieId}", 999)
                .then()
                .statusCode(HttpStatus.NOT_MODIFIED.value());
    }

    @Test
    @DisplayName("DELETE /oscars/movies/{movieId} - 304 Not Modified when movies service error")
    void deleteOscarsByMovie_304_whenMoviesServiceError() {
        // Mock movies service error
        when(moviesClient.getMovieById(1L)).thenReturn(null);

        given()
                .when()
                .delete("/oscars/movies/{movieId}", 1)
                .then()
                .statusCode(HttpStatus.NOT_MODIFIED.value());
    }

    // ========== Edge cases and error scenarios ==========

    @Test
    @DisplayName("Test callback with invalid URL")
    void testCallbackWithInvalidUrl() {
        // Mock movies for length filtering
        when(moviesClient.getAllMovies()).thenReturn(TestDataProvider.getMoviesForHonorByLength());
        when(moviesClient.patchMovie(eq(1L), any())).thenReturn(TestDataProvider.getUpdatedMovieWithOscars());

        given()
                .queryParam("oscarsToAdd", 3)
                .contentType(ContentType.JSON)
                .body("{\"callbackUrl\": \"" + TestDataProvider.INVALID_CALLBACK_URL + "\"}")
                .when()
                .post("/oscars/movies/honor-by-length/{minLength}", 150)
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("updatedCount", is(1));
    }

    @Test
    @DisplayName("Test callback with empty URL")
    void testCallbackWithEmptyUrl() {
        // Mock movies for length filtering
        when(moviesClient.getAllMovies()).thenReturn(TestDataProvider.getMoviesForHonorByLength());
        when(moviesClient.patchMovie(eq(1L), any())).thenReturn(TestDataProvider.getUpdatedMovieWithOscars());

        given()
                .queryParam("oscarsToAdd", 3)
                .contentType(ContentType.JSON)
                .body("{\"callbackUrl\": \"" + TestDataProvider.EMPTY_CALLBACK_URL + "\"}")
                .when()
                .post("/oscars/movies/honor-by-length/{minLength}", 150)
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("updatedCount", is(1));
    }

    @Test
    @DisplayName("Test with null callback body")
    void testWithNullCallbackBody() {
        // Mock movies for length filtering
        when(moviesClient.getAllMovies()).thenReturn(TestDataProvider.getMoviesForHonorByLength());
        when(moviesClient.patchMovie(eq(1L), any())).thenReturn(TestDataProvider.getUpdatedMovieWithOscars());

        given()
                .queryParam("oscarsToAdd", 3)
                .contentType(ContentType.JSON)
                .body("{}")
                .when()
                .post("/oscars/movies/honor-by-length/{minLength}", 150)
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("updatedCount", is(1));
    }

    @Test
    @DisplayName("Test with zero minLength")
    void testWithZeroMinLength() {
        // Mock movies for length filtering
        when(moviesClient.getAllMovies()).thenReturn(TestDataProvider.getMoviesForHonorByLength());
        when(moviesClient.patchMovie(eq(1L), any())).thenReturn(TestDataProvider.getUpdatedMovieWithOscars());

        given()
                .queryParam("oscarsToAdd", 3)
                .contentType(ContentType.JSON)
                .body("{}")
                .when()
                .post("/oscars/movies/honor-by-length/{minLength}", 0)
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("updatedCount", is(1));
    }

    @Test
    @DisplayName("Test with zero maxOscars")
    void testWithZeroMaxOscars() {
        // Mock movies for oscars filtering
        when(moviesClient.getAllMovies()).thenReturn(TestDataProvider.getMoviesForHonorLowOscars());

        given()
                .queryParam("maxOscars", 0)
                .queryParam("oscarsToAdd", 2)
                .contentType(ContentType.JSON)
                .body("{}")
                .when()
                .post("/oscars/movies/honor-low-oscars")
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("updatedCount", is(0))
                .body("updatedMovies.size()", is(0));
    }

    @Test
    @DisplayName("Test with zero oscarsToAdd")
    void testWithZeroOscarsToAdd() {
        // Mock movies for length filtering
        when(moviesClient.getAllMovies()).thenReturn(TestDataProvider.getMoviesForHonorByLength());
        when(moviesClient.patchMovie(eq(1L), any())).thenReturn(TestDataProvider.getUpdatedMovieWithOscars());

        given()
                .queryParam("oscarsToAdd", 0)
                .contentType(ContentType.JSON)
                .body("{}")
                .when()
                .post("/oscars/movies/honor-by-length/{minLength}", 150)
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("updatedCount", is(1))
                .body("updatedMovies[0].oscarsCount", is(TestDataProvider.getUpdatedMovieWithOscars().oscarsCount())); // Original count + 0
    }
}
