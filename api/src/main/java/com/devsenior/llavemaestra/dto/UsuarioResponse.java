package com.devsenior.llavemaestra.dto;

import java.time.Instant;

import com.devsenior.llavemaestra.model.Rol;
import com.devsenior.llavemaestra.model.Usuario;

public record UsuarioResponse(
        Long id,
        String nombre,
        String email,
        Rol rol,
        boolean activo,
        Instant creadoEn
) {
    public static UsuarioResponse fromEntity(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getRol(),
                usuario.isActivo(),
                usuario.getCreadoEn()
        );
    }
}