package com.devsenior.llavemaestra.dto;

import com.devsenior.llavemaestra.model.Categoria;

public record HerramientaResponse(
        Long id,
        String nombre,
        Categoria categoria,
        String descripcion,
        int precioDia,
        int deposito,
        int unidades,
        String imagenUrl,
        boolean activa
    ) {

}
