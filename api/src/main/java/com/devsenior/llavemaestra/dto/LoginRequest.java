package com.devsenior.llavemaestra.dto;

import com.fasterxml.jackson.annotation.JsonAlias;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
    @JsonAlias("username") String email,
    @JsonAlias("email") String username,
    @NotBlank(message = "La contraseña es obligatoria") String password
) {
    public String getIdentificador() {
        if (email != null && !email.isBlank()) {
            return email;
        }
        return username;
    }
}
