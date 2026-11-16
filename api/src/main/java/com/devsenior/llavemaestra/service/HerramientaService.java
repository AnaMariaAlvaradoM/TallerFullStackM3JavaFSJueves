package com.devsenior.llavemaestra.service;

import com.devsenior.llavemaestra.dto.FranjaOcupada;
import com.devsenior.llavemaestra.dto.HerramientaRequest;
import com.devsenior.llavemaestra.dto.HerramientaResponse;

import java.time.LocalDate;
import java.util.List;

import org.springframework.security.core.Authentication;

public interface HerramientaService {
    
    List<HerramientaResponse> listar(Authentication autenticacion);

    HerramientaResponse buscarPorId(Long id, Authentication autenticacion);

    HerramientaResponse crear(HerramientaRequest request);

    HerramientaResponse actualizar(Long id, HerramientaRequest request);

    HerramientaResponse cambiarEstado(Long id, boolean activa);

    List<FranjaOcupada> ocupacion(Long id, LocalDate fecha);
}
