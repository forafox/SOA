package com.blps.controller;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import lombok.extern.slf4j.Slf4j;

@Path("/hello")
@Slf4j
public class HelloController {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        log.info("Hello endpoint called");
        return "Hello, JAX-RS!";
    }
}
