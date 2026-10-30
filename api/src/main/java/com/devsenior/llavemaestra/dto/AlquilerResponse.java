package com.devsenior.llavemaestra.dto;

import com.devsenior.llavemaestra.model.Categoria;
import com.devsenior.llavemaestra.model.EstadoAlquiler;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record AlquilerResponse(
        Long id,
        Long herramientaId,
        String herramientaNombre,
        Categoria categoria,
        Long clienteId,
        String clienteNombre,
        LocalDate fecha,
        LocalDate diaInicio,
        LocalDate diaFin,
        String nota,
        int valorTotal,
        EstadoAlquiler estado,
        LocalDateTime creadaEn




) {
}
