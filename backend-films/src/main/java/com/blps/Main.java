package com.blps;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

import java.net.URI;

public class Main {

    public static HttpServer startServer() {


        ResourceConfig rc = new ResourceConfig()
                .register(MovieResource.class)
                .register(JacksonFeature.class)
                .register(ObjectMapperProvider.class);

        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

    public static final String BASE_URI = "http://0.0.0.0:8081/api/";


    public static void main(String[] args) {
        final HttpServer server = startServer();


        try {
            Thread.currentThread().join(); // держим сервер
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}