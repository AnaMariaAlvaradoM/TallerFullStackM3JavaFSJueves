package com.devsenior.llavemaestra.service;

import java.util.List;

import com.devsenior.llavemaestra.dto.AlquilerRequest;
import com.devsenior.llavemaestra.dto.AlquilerResponse;

public interface AlquilerService {
    AlquilerResponse crearAlquiler(AlquilerRequest request);
    List<AlquilerResponse> obtenerMisAlquileres();
    AlquilerResponse cancelarAlquiler(Long id);
    List<AlquilerResponse> obtenerTodos(String estado);
    AlquilerResponse entregarAlquiler(Long id);
    AlquilerResponse devolverAlquiler(Long id);
}
