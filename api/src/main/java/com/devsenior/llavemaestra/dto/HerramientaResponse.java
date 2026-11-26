package com.devsenior.llavemaestra.dto;

import com.devsenior.llavemaestra.model.TipoCategoria;

public record HerramientaResponse(

    Long id,
    String nombre,
    TipoCategoria categoria,
    int deposito,
    int precioDia,
    String descripcion,
    int unidades,
    String imagenUrl,
    boolean activa
) {

}
