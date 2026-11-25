package com.devsenior.llavemaestra.dto;

import jakarta.validation.constraints.NotNull;

public record EstadoRequest(

    @NotNull(message = "Indica si queda activo (true) o inactivo (false)")
    Boolean activo
) {

}
