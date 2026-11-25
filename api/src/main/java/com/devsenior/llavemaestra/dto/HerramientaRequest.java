package com.devsenior.llavemaestra.dto;

import com.devsenior.llavemaestra.model.Categoria;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record HerramientaRequest(

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 80, message = "El nombre admite máximo 80 caracteres")
    String nombre,

    @NotNull(message = "La categoria es obligatoria")
    Categoria categoria,

    @Min(value = 15000, message = "El precio por dia mínimo es $15000")
    int precioDia,

    @Min(value = 10000, message = "El deposito mínimo es $10000")
    int deposito,

    @Min(value = 1, message = "La cantidad mínima es 1 herramienta")
    @Max(value = 4, message = "La cantidad máxima es 4 herramientas")
    int unidades,

    @Size(max = 500, message = "La descripción admite máximo 500 caracteres")
    String descripcion,

    @Size(max = 300, message = "La URL de la imagen admite máximo 300 caracteres")
    String imagenUrl
) {

}
