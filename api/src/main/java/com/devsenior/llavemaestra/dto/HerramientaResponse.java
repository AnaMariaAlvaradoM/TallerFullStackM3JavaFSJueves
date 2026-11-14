package com.devsenior.llavemaestra.dto;

import com.devsenior.llavemaestra.model.Categoria;

public record HerramientaResponse(
        Long id,
        String nombre,
        Categoria categoria,
        int precioDia,
        int deposito,
        String descripcion,
        String imagenUrl,
        int unidades,
        boolean activo


) {
}
