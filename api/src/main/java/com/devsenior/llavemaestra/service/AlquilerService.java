package com.devsenior.llavemaestra.service;

import com.devsenior.llavemaestra.dto.AlquilerRequest;
import com.devsenior.llavemaestra.dto.AlquilerResponse;
import org.springframework.security.core.Authentication;


import java.time.LocalDate;
import java.util.List;

public interface AlquilerService {
    List<AlquilerResponse> listarMias(String email);
    List<AlquilerResponse> listarTodas(LocalDate fecha);
    AlquilerResponse crear(AlquilerRequest request, String email);
    AlquilerResponse cancelar(Long id, Authentication autentication);
}
