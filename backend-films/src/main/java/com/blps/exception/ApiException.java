package com.blps.exception;

import jakarta.ws.rs.core.Response;
import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {

    private final Response.Status status;

    public ApiException(Response.Status status, String message) {
        super(message);
        this.status = status;
    }

}


