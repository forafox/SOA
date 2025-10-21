package com.jellyone.oscars.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/saml")
public class SamlController {
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> status(@AuthenticationPrincipal(expression = "username") String username) {
        Map<String, Object> result = new HashMap<>();
        boolean authenticated = username != null;
        result.put("authenticated", authenticated);
        if (authenticated) {
            result.put("user", username);
        }
        return ResponseEntity.ok(result);
    }
}
