package com.blps;

import com.blps.config.CorsFilter;
import com.blps.config.ObjectMapperProvider;
import com.blps.controller.MovieController;
import lombok.extern.slf4j.Slf4j;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

import java.net.URI;

@Slf4j
public class Main {

    public static final String BASE_URI = "http://localhost:8081/api/";

    public static void main(String[] args) {
        log.info("Starting Movies API server...");
        startServer();
        log.info("Server started at " + BASE_URI);

        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static void startServer() {
        ResourceConfig rc = new ResourceConfig()
                .register(MovieController.class)
                .register(JacksonFeature.class)
                .register(ObjectMapperProvider.class)
                .register(CorsFilter.class);

        GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }
}