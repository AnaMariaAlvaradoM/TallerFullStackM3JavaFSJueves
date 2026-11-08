package com.devsenior.llavemaestra.dto;

import jakarta.validation.constraints.NotBlank;

// Lo que llega en POST /api/auth/login
public record LoginRequest(
        @NotBlank(message = "El email es obligatorio") String email,
        @NotBlank(message = "La contraseña es obligatoria") String password) {
}
