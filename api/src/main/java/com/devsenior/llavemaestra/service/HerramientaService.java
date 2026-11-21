package com.devsenior.llavemaestra.service;

import java.util.List;

import com.devsenior.llavemaestra.dto.HerramientaRequest;
import com.devsenior.llavemaestra.dto.HerramientaResponse;

public interface HerramientaService {
    List<HerramientaResponse> listarHerramientas();
    HerramientaResponse buscarPorId(Long id);
    HerramientaResponse cambiarEstado(Long id, boolean activa);
    HerramientaResponse crear(HerramientaRequest request);
    HerramientaResponse actualizar(Long id, HerramientaRequest request);
}
