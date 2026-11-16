package com.devsenior.llavemaestra.dto;

import com.devsenior.llavemaestra.model.TipoHerramienta;

public record HerramientaResponse(
    Long id,
    String nombre,
    TipoHerramienta categoria,
    String descripcion,
    int precioDia,
    int deposito,
    int unidades,
    String imagenUrl,
    boolean activa
) {

}
