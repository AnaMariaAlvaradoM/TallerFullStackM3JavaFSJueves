package com.devsenior.llavemaestra.dto;

import com.devsenior.llavemaestra.model.Rol;
import com.devsenior.llavemaestra.model.Usuario;

import java.time.LocalDateTime;

// Lo que ve el frontend de una cuenta. OJO: NO trae la contrasena.
public record UsuarioResponse(
        Long id,
        String nombre,
        String email,
        Rol rol,
        boolean activo,
        LocalDateTime creadoEn) {

    public static UsuarioResponse desde(Usuario u) {
        return new UsuarioResponse(u.getId(), u.getNombre(), u.getEmail(), u.getRol(), u.isActivo(), u.getCreadoEn());
    }
}
