package com.blps;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
//import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJaxbJsonProvider;


import java.net.URI;

public class Main {
    public static final String BASE_URI = "http://localhost:8080/api/";

    public static HttpServer startServer() {

        final ResourceConfig rc = new ResourceConfig()
                .packages("com.blps");

        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

    public static void main(String[] args) {
        final HttpServer server = startServer();


        try {
            Thread.currentThread().join(); // держим сервер
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}