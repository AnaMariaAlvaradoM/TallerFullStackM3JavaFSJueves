package com.devsenior.llavemaestra.dto;

import jakarta.validation.constraints.NotNull;

public record EstadoRequest(
        @NotNull(message = "Indica si queda activa o inactivo")
        Boolean activa
) {
}
