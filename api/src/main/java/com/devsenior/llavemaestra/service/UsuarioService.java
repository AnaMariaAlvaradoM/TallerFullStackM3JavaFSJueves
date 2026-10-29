package com.devsenior.llavemaestra.service;

import com.devsenior.llavemaestra.dto.UsuarioRequest;
import com.devsenior.llavemaestra.dto.UsuarioResponse;

import java.util.List;

public interface UsuarioService {
    List<UsuarioResponse> listar();
    UsuarioResponse crear(UsuarioRequest request);
    UsuarioResponse cambiarEstado (Long id, boolean activo, String emailDeQuienPide);

}
