package com.devsenior.llavemaestra.controller;

import com.devsenior.llavemaestra.dto.LoginRequest;
import com.devsenior.llavemaestra.dto.TokenResponse;
import com.devsenior.llavemaestra.service.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Publico: no pide token (ver SecurityConfig)
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest datos) {
        return authService.login(datos);
    }
}
