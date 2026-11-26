package com.devsenior.llavemaestra.dto;

import jakarta.validation.constraints.NotNull;

// Activar o desactivar un herramienta o una cuenta: { "activa": false }
public record EstadoRequest(
        @NotNull(message = "Indica si queda activo (true) o inactivo (false)")
        Boolean activa
) {
}
