package com.devsenior.llavemaestra.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.security.core.Authentication;

import com.devsenior.llavemaestra.dto.AlquilerRequest;
import com.devsenior.llavemaestra.dto.AlquilerResponse;

public interface AlquilerService {

    List<AlquilerResponse> listarMias(String email);

    List<AlquilerResponse> listarTodas(LocalDate fecha);

    AlquilerResponse crear(AlquilerRequest request, String email);

    AlquilerResponse cancelar(Long id, Authentication autenticacion);

}
