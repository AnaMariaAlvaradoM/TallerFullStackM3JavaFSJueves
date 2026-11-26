package com.devsenior.llavemaestra.service;

import java.util.List;

import com.devsenior.llavemaestra.dto.HerramientaRequest;
import com.devsenior.llavemaestra.dto.HerramientaResponse;

public interface HerramientaService {
    List<HerramientaResponse> listar(boolean puedeGestionar);
    HerramientaResponse obtenerPorId(Long id, boolean puedeGestionar);
    HerramientaResponse crear(HerramientaRequest request);
    HerramientaResponse actualizar(Long id, HerramientaRequest request);
    HerramientaResponse cambiarEstado(Long id, boolean activa);
}