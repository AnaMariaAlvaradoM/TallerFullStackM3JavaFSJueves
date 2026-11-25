package com.devsenior.llavemaestra.service;

import java.util.List;

import com.devsenior.llavemaestra.dto.AlquilerRequest;
import com.devsenior.llavemaestra.dto.AlquilerResponse;
import com.devsenior.llavemaestra.model.EstadoAlquiler;

public interface AlquilerService {
    AlquilerResponse crear(AlquilerRequest request, String emailCliente);
    List<AlquilerResponse> listarMios(String emailCliente);
    List<AlquilerResponse> listarTodos(EstadoAlquiler estado);
    AlquilerResponse cancelar(Long id, String emailAutenticado, boolean puedeGestionarTodos);
    AlquilerResponse entregar(Long id);
    AlquilerResponse devolver(Long id);
}