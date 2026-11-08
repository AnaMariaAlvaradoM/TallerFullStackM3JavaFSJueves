package com.devsenior.llavemaestra.dto;

import jakarta.validation.constraints.NotNull;

// Activar o desactivar una herramienta o una cuenta: { "activo": false }
public record EstadoHerramientaRequest(
        @NotNull(message = "Indica si queda activa (true) o inactiva (false)") 
        Boolean activa
    ) {

}
