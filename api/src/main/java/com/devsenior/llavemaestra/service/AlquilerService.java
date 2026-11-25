package com.devsenior.llavemaestra.service;

import java.util.List;
import org.springframework.security.core.Authentication;
import com.devsenior.llavemaestra.dto.AlquilerRequest;
import com.devsenior.llavemaestra.dto.AlquilerResponse;

public interface AlquilerService {

    AlquilerResponse crear(AlquilerRequest request, String email);

    List<AlquilerResponse> listarMios(String email);
    
    AlquilerResponse cancelar(Long id, Authentication autenticacion);
}
