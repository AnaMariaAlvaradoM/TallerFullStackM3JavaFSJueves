package com.devsenior.llavemaestra.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

// Cuerpo de POST /api/alquileres. SOLO trae esto:
// el cliente sale del token y el valor lo calcula el backend.
// Si el JSON trae otros campos (clienteId, valorTotal...), simplemente se ignoran.
public record AlquilerRequest(
        @NotNull(message = "Falta la herramienta que quieres alquilar") Long herramientaId,
        @NotNull(message = "Falta la fecha de inicio") LocalDate fechaInicio,
        @NotNull(message = "Falta la fecha de fin") LocalDate fechaFin) {
}
