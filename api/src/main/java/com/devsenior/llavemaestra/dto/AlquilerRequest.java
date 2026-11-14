package com.devsenior.llavemaestra.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record AlquilerRequest(
        @NotNull(message = "La herramienta es obligatoria")
        Long herramientaId,

        @NotNull(message = "La fecha de inicio es obligatoria")
        LocalDate fechaInicio,
        @NotNull(message = "La fecha de retorno es  obligatoria")
        LocalDate fechaFin
) {
}
