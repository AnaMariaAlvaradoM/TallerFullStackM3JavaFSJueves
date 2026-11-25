package com.devsenior.llavemaestra.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;

public record AlquilerRequest(

    @NotNull(message = "Indica la herramienta") Long herramientaId,
    @NotNull(message = "Indica la fecha de inicio") LocalDate fechaInicio,
    @NotNull(message = "Indica la fecha de fin") LocalDate fechaFin

) {

}
