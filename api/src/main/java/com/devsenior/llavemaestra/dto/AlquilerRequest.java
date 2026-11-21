package com.devsenior.llavemaestra.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;

public record AlquilerRequest(
    @NotNull(message = "Falta la herramienta")
    Long herramientaId,

    @NotNull(message = "Falta la fecha de inicio")
    LocalDate fechaInicio,

    @NotNull(message = "Falta la fecha de fin")
    LocalDate fechaFin,

    Long clienteId,
    Integer valorTotal,
    Integer deposito
) {}
