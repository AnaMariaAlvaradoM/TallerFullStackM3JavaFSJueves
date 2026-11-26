package com.devsenior.llavemaestra.dto;

import java.math.BigDecimal;

import com.devsenior.llavemaestra.model.CategoriaHerramienta;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record HerramientaRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 80, message = "El nombre no puede superar 80 caracteres")
        String nombre,

        @NotNull(message = "La categoría es obligatoria")
        CategoriaHerramienta categoria,

        @Size(max = 300, message = "La descripción no puede superar 300 caracteres")
        String descripcion,

        @NotNull(message = "El precio por día es obligatorio")
        @DecimalMin(value = "1000", message = "El precio por día debe ser al menos 1000")
        BigDecimal precioDia,

        @NotNull(message = "El depósito es obligatorio")
        @DecimalMin(value = "0", message = "El depósito no puede ser negativo")
        BigDecimal deposito,

        @NotNull(message = "Las unidades son obligatorias")
        @Min(value = 1, message = "Las unidades deben ser al menos 1")
        @Max(value = 50, message = "Las unidades no pueden superar 50")
        Integer unidades,

        @Size(max = 300, message = "La URL de imagen no puede superar 300 caracteres")
        String imagenUrl
) {
}