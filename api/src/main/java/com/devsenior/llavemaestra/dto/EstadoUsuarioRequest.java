package com.devsenior.llavemaestra.dto;

import jakarta.validation.constraints.NotNull;

public record EstadoUsuarioRequest(
    @NotNull(message = "Indica si la cuenta queda activa (true o false)")boolean activo) {

}
