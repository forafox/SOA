package com.blps;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.Provider;
import lombok.RequiredArgsConstructor;

@Provider
@RequiredArgsConstructor
public class ObjectMapperProvider implements ContextResolver<ObjectMapper> {

    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Override
    public ObjectMapper getContext(Class<?> type) {
        return mapper;
    }
}