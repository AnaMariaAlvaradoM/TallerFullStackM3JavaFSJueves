package com.devsenior.llavemaestra.dto;

import java.math.BigDecimal;

import com.devsenior.llavemaestra.model.CategoriaHerramienta;
import com.devsenior.llavemaestra.model.Herramienta;

public record HerramientaResponse(
        Long id,
        String nombre,
        CategoriaHerramienta categoria,
        String descripcion,
        BigDecimal precioDia,
        BigDecimal deposito,
        int unidades,
        String imagenUrl,
        boolean activa
) {
    public static HerramientaResponse fromEntity(Herramienta herramienta) {
        return new HerramientaResponse(
                herramienta.getId(),
                herramienta.getNombre(),
                herramienta.getCategoria(),
                herramienta.getDescripcion(),
                herramienta.getPrecioDia(),
                herramienta.getDeposito(),
                herramienta.getUnidades(),
                herramienta.getImagenUrl(),
                herramienta.isActiva()
        );
    }
}