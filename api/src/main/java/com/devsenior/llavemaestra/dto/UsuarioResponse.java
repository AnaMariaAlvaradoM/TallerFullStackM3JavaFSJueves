package com.devsenior.llavemaestra.dto;

import java.time.LocalDateTime;

import com.devsenior.llavemaestra.model.Rol;

public record UsuarioResponse(

        Long id,
        String nombre,
        String email,
        Rol rol,
        boolean activo,
        LocalDateTime creadoEn
) {

}
