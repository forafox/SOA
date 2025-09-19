package com.blps;

import com.blps.controller.MovieController;
import com.blps.exception.ApiExceptionMapper;
import com.blps.exception.GenericExceptionMapper;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/api")
public class RestApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new HashSet<>();
        resources.add(MovieController.class);
        resources.add(ApiExceptionMapper.class);
        resources.add(GenericExceptionMapper.class);
        return resources;
    }
}