package com.devsenior.llavemaestra.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.devsenior.llavemaestra.model.EstadoAlquiler;

public record AlquilerResponse(
    Long id,
    Long herramientaId,
    String herramientaNombre,
    Long clienteId,
    String clienteNombre,
    LocalDate fechaInicio,
    LocalDate fechaFin,
    int dias,
    BigDecimal valorTotal,
    BigDecimal deposito,
    EstadoAlquiler estado,
    LocalDateTime creadoEn
) {}
