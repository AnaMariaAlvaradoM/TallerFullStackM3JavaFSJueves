package com.devsenior.llavemaestra.dto;

import jakarta.validation.constraints.NotNull;

public record EstadoUsuarioRequest(
        @NotNull(message = "El campo activo es obligatorio")
        Boolean activo
) {
}