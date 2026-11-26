package com.devsenior.llavemaestra.dto;

import java.math.BigDecimal;

import com.devsenior.llavemaestra.model.Categoria;

public record HerramientaResponse(
    Long id,
    String nombre,
    Categoria categoria,
    String descripcion,
    BigDecimal precioDia,
    BigDecimal deposito,
    Integer unidades,
    String imagenUrl,
    boolean activa
) {}
