package com.devsenior.llavemaestra.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;

public record AlquilerRequest(
        @NotNull(message = "La herramienta es obligatoria")
        Long herramientaId,

        @NotNull(message = "La fecha de inicio es obligatoria")
        LocalDate fechaInicio,

        @NotNull(message = "La fecha de fin es obligatoria")
        LocalDate fechaFin
) {
}