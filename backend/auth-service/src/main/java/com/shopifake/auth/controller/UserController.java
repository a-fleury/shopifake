package com.shopifake.auth.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserController {

    // Endpoint protégé: nécessite un JWT valide (non listé en permitAll dans SecurityConfig)
    @GetMapping("/protected/ping")
    public Map<String, String> protectedPing() {
        return Map.of("message", "pong");
    }

    // Retourne des infos simples sur l'utilisateur authentifié
    @GetMapping("/me")
    public Map<String, Object> me(Authentication authentication) {
        boolean isAuth = authentication != null && authentication.isAuthenticated();
        String name = authentication != null ? authentication.getName() : null;
        return Map.of(
                "authenticated", isAuth,
                "name", name
        );
    }
}
