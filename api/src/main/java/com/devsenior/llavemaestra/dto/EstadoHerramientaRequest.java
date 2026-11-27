package com.devsenior.llavemaestra.dto;

import jakarta.validation.constraints.NotNull;

public record EstadoHerramientaRequest(
    @NotNull(message = "Indica si la herramienta queda activa (true o false)") Boolean activa) {

}
