package com.devsenior.llavemaestra.dto;

import java.time.LocalDateTime;

import com.devsenior.llavemaestra.model.Rol;
import com.devsenior.llavemaestra.model.Usuario;

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