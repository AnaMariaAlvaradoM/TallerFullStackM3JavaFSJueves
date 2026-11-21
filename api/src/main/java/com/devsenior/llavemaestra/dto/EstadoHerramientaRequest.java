package com.devsenior.llavemaestra.dto;

import jakarta.validation.constraints.NotNull;

public record EstadoHerramientaRequest(
    @NotNull(message = "El campo activa es obligatorio")
    Boolean activa
) {

}
