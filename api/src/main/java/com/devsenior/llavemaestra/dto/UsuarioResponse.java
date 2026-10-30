package com.devsenior.llavemaestra.dto;

import com.devsenior.llavemaestra.model.Rol;

import java.time.LocalDateTime;

public record UsuarioResponse(
        Long id,
        String nombre,
        String email,
        Rol rol,
        boolean activa,
        LocalDateTime creadoEn
) {
}
