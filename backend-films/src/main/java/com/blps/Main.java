package com.blps;

import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

import java.net.URI;

public class Main {

    public static final String BASE_URI = "http://localhost:8080/api/";


    public static void main(String[] args) {
        startServer();

        try {
            Thread.currentThread().join(); // держим сервер
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static void startServer() {
        ResourceConfig rc = new ResourceConfig()
                .register(MovieResource.class)
                .register(JacksonFeature.class)
                .register(ObjectMapperProvider.class);

        GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }
}