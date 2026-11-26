package com.devsenior.llavemaestra.dto;

import java.time.LocalDateTime;

public record ErrorResponse(int codigo, String mensaje, LocalDateTime fecha) {
}
