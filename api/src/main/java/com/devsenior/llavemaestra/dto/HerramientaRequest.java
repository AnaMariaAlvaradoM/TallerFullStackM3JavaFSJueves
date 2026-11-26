package com.devsenior.llavemaestra.dto;

import com.devsenior.llavemaestra.model.TipoCategoria;

//import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record HerramientaRequest(

    @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 80, message = "El nombre admite máximo 80 caracteres")
        String nombre,

        @NotNull(message = "El tipo es obligatorio")
        TipoCategoria categoria,

        @Min(value = 1000, message = "El depósito no puede ser negativo")
        int deposito,

        @Min(value = 1000, message = "El precio por día mínimo es $1.000")
        int precioDia,

        @Size(max = 500, message = "La descripción admite máximo 500 caracteres")
        String descripcion,

        @Min(value = 1, message = "Debe haber al menos una unidad disponible")
        int unidades,

        @Size(max = 300, message = "La URL de la imagen admite máximo 300 caracteres")
        String imagenUrl
) {

}
