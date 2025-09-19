package com.jellyone.oscars.exception;

import com.jellyone.oscars.model.Error;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<Error> handleHttpClientError(HttpClientErrorException ex) {
        HttpStatusCode status = ex.getStatusCode();
        if (status == HttpStatus.NO_CONTENT) {
            return ResponseEntity.noContent().build();
        }
        if (status == HttpStatus.UNPROCESSABLE_ENTITY) {
            return buildErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY, "Validation error: " + ex.getResponseBodyAsString());
        }
        return buildErrorResponse(status, "Client error: " + ex.getMessage());
    }

    @ExceptionHandler(HttpServerErrorException.class)
    public ResponseEntity<Error> handleHttpServerError(HttpServerErrorException ex) {
        return buildErrorResponse(ex.getStatusCode(), "External service error: " + ex.getMessage());
    }

    @ExceptionHandler({IllegalArgumentException.class, MethodArgumentNotValidException.class})
    public ResponseEntity<Error> handleValidationExceptions(Exception ex) {
        if (ex instanceof MethodArgumentNotValidException validationEx) {
            String message = validationEx.getBindingResult().getFieldErrors().stream()
                    .map(err -> err.getField() + " " + err.getDefaultMessage())
                    .findFirst()
                    .orElse("Validation failed");
            return buildErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY, "Validation error: " + message);
        }
        return buildErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY, "Validation error: " + ex.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Error> handleMalformedJson(HttpMessageNotReadableException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Malformed JSON request: " + ex.getMostSpecificCause().getMessage());
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Error> handleResponseStatus(ResponseStatusException ex) {
        return buildErrorResponse(ex.getStatusCode(), ex.getReason());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Error> handleGeneric(Exception ex) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error: " + ex.getMessage());
    }

    private ResponseEntity<Error> buildErrorResponse(HttpStatusCode status, String message) {
        return ResponseEntity.status(status).body(new Error(message));
    }
}