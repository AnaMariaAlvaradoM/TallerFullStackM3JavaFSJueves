package com.devsenior.llavemaestra.dto;

import com.devsenior.llavemaestra.model.Categoria;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record HerramientaRequest(

    @NotBlank(message = "Debe de ingresar el nombre de la herrmaienta.")
    @Size(min = 5,max = 30,message = "El nombre debe de tener minimo 5 caracteres y maximo 30.")
    String nombre,

    @NotBlank(message = "La categoria es obligatoria")
    Categoria categoria,
    @Min(value = 1500, message = "El precio por día minimo es  $15.000")
    Integer precioDia,
    @Min(value = 50000, message = "El precio minimo del deposito es es $50.000")
    Integer deposito,

    @Size(max = 500, message = "La descripción admite máximo 500 caracteres")
    String descripcion,

    @Size(max = 300, message = "La URL de la imagen admite máximo 300 caracteres")
    String imagenUrl,
    @Min(value = 0, message = "La cantidad minima de unidades es 0.")
    Integer unidades
) {
}
