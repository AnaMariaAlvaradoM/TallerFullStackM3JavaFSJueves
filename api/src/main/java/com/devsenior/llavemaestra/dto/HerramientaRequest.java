package com.devsenior.llavemaestra.dto;

import java.math.BigDecimal;

import com.devsenior.llavemaestra.model.Categoria;

public record HerramientaRequest(
    String nombre,
    Categoria categoria,
    String descripcion,
    BigDecimal precioDia,
    BigDecimal deposito,
    Integer unidades,
    String imagenUrl,
    Boolean activa
) {}
