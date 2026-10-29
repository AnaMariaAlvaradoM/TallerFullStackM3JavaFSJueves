package com.devsenior.llavemaestra.service;

import com.devsenior.llavemaestra.dto.HerramientaResponse;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface HerramientaService {
List<HerramientaResponse>listar(Authentication authentication);
HerramientaResponse buscarPorId(Long id, Authentication authentication);
HerramientaResponse cambiarEstado(Long id, boolean activo);

}
