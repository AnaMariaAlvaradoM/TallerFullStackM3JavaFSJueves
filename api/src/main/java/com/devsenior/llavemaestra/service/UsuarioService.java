package com.devsenior.llavemaestra.service;

import java.util.List;

import com.devsenior.llavemaestra.dto.UsuarioResponse;

public interface UsuarioService {
    
    List<UsuarioResponse> listar();

    UsuarioResponse cambiarEstado(Long id, boolean activo, String emailDeQuienPide);
}
