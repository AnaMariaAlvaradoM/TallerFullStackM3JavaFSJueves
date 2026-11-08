package com.devsenior.llavemaestra.dto;

import com.devsenior.llavemaestra.model.Alquiler;
import com.devsenior.llavemaestra.model.EstadoAlquiler;

import java.time.LocalDate;
import java.time.LocalDateTime;

// Lo que ve el frontend de un alquiler (plano: sin la entidad completa de herramienta ni de cliente)
public record AlquilerResponse(
        Long id,
        Long herramientaId,
        String herramientaNombre,
        Long clienteId,
        String clienteNombre,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        Integer dias,
        Integer valorTotal,
        Integer deposito,
        EstadoAlquiler estado,
        LocalDateTime creadoEn) {

    public static AlquilerResponse desde(Alquiler a) {
        return new AlquilerResponse(
                a.getId(),
                a.getHerramienta().getId(),
                a.getHerramienta().getNombre(),
                a.getCliente().getId(),
                a.getCliente().getNombre(),
                a.getFechaInicio(),
                a.getFechaFin(),
                a.getDias(),
                a.getValorTotal(),
                a.getDeposito(),
                a.getEstado(),
                a.getCreadoEn());
    }
}
