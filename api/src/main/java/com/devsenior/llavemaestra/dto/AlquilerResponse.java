package com.devsenior.llavemaestra.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import com.devsenior.llavemaestra.model.Alquiler;
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
        Instant creadoEn
) {
    public static AlquilerResponse fromEntity(Alquiler alquiler) {
        return new AlquilerResponse(
                alquiler.getId(),
                alquiler.getHerramienta().getId(),
                alquiler.getHerramienta().getNombre(),
                alquiler.getCliente().getId(),
                alquiler.getCliente().getNombre(),
                alquiler.getFechaInicio(),
                alquiler.getFechaFin(),
                alquiler.getDias(),
                alquiler.getValorTotal(),
                alquiler.getDeposito(),
                alquiler.getEstado(),
                alquiler.getCreadoEn()
        );
    }
}