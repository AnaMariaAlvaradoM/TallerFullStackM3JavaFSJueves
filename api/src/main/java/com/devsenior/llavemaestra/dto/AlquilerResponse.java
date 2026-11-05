package com.devsenior.llavemaestra.dto;

import com.devsenior.llavemaestra.model.EstadoAlquiler;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record AlquilerResponse(
        Long id,
        Long herramientaId,
        String herramientaNombre,
        Long clienteId,
        String clienteNombre,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        long dias,
        int valorTotal,
        int deposito,
        EstadoAlquiler estado,
        LocalDateTime creadoEn




) {
}
