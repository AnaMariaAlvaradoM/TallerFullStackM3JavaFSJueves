package com.devsenior.llavemaestra.service;

import java.util.List;

import org.springframework.security.core.Authentication;


import com.devsenior.llavemaestra.dto.HerramientaRequest;
import com.devsenior.llavemaestra.dto.HerramientaResponse;

public interface HerramientaService {

    List<HerramientaResponse> listar(Authentication authentication);

    
    HerramientaResponse buscarPorId(Long id, Authentication autenticacion);

    HerramientaResponse crear(HerramientaRequest request);

    HerramientaResponse actualizar(Long id, HerramientaRequest request);

    HerramientaResponse cambiarEstado(Long id, boolean activa);

        

}
