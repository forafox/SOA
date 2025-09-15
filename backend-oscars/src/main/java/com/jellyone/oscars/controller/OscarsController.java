package com.jellyone.oscars.controller;

import com.jellyone.oscars.model.Person;
import com.jellyone.oscars.service.OscarsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class OscarsController {
    private final OscarsService service;

    @GetMapping("/oscars/operators/losers")
    public ResponseEntity<List<Person>> getOscarLosers() {
        List<Person> losers = service.getOscarLosers();
        return losers.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(losers);
    }

    @PostMapping("/oscars/movies/honor-by-length/{minLength}")
    public ResponseEntity<Map<String, Object>> honorMoviesByLength(
            @PathVariable double minLength,
            @RequestParam int oscarsToAdd
    ) {
        Map<String, Object> result = service.honorMoviesByLength(minLength, oscarsToAdd);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/oscars/movies/honor-low-oscars")
    public ResponseEntity<Map<String, Object>> honorMoviesWithFewOscars(
            @RequestParam int maxOscars,
            @RequestParam int oscarsToAdd
    ) {
        Map<String, Object> result = service.honorMoviesWithFewOscars(maxOscars, oscarsToAdd);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/oscars/movies/{movieId}")
    public ResponseEntity<List<Map<String, Object>>> getOscarsByMovie(
            @PathVariable long movieId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        List<Map<String, Object>> oscars = service.getOscarsByMovie(movieId, page, size);
        return oscars.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(oscars);
    }

    @PostMapping("/oscars/movies/{movieId}")
    public ResponseEntity<Map<String, Object>> addOscars(
            @PathVariable long movieId,
            @RequestParam int oscarsToAdd
    ) {
        Map<String, Object> result = service.addOscars(movieId, oscarsToAdd);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/oscars/movies/{movieId}")
    public ResponseEntity<Void> deleteOscarsByMovie(@PathVariable long movieId) {
        boolean changed = service.deleteOscarsByMovie(movieId);
        return changed
                ? ResponseEntity.noContent().build()
                : ResponseEntity.status(304).build();
    }
}


