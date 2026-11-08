package com.devsenior.llavemaestra.dto;

import jakarta.validation.constraints.NotNull;

// Cuerpo de PATCH /api/usuarios/{id}/estado: { "activo": false }
public record EstadoUsuarioRequest(
        @NotNull(message = "Indica si la cuenta queda activa (true o false)") Boolean activo) {
}
