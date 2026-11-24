package com.devsenior.llavemaestra.dto;

import jakarta.validation.constraints.NotNull;

public record EstadoRequest(@NotNull Boolean activa) {
}