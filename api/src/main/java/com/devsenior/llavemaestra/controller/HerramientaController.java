package com.devsenior.llavemaestra.controller;


import com.devsenior.llavemaestra.dto.EstadoRequest;
import com.devsenior.llavemaestra.dto.HerramientaResponse;
import com.devsenior.llavemaestra.service.HerramientaService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/herramientas")
public class HerramientaController {
    private final HerramientaService herramientaService;


    public HerramientaController(HerramientaService herramientaService) {
        this.herramientaService = herramientaService;
    }

    @PreAuthorize("hasAuthority('HERRAMIENTAS_VER')")
    @GetMapping
    public ResponseEntity<List<HerramientaResponse>> listar(Authentication autentication){
        return ResponseEntity.ok(herramientaService.listar(autentication));
    }

    @PreAuthorize("hasAuthority('HERRAMIENTAS_VER')")
    @GetMapping("/{id}")
    public ResponseEntity<HerramientaResponse> buscarId(@PathVariable Long id, Authentication autentication){
        return ResponseEntity.ok(herramientaService.buscarPorId(id,autentication));
    }

    @PreAuthorize("hasAuthority('HERRAMIENTAS_GESTIONAR')")
    @PatchMapping("/{id}/estado")
    public ResponseEntity<HerramientaResponse> cambiarEstado(@PathVariable Long id, @Valid @RequestBody EstadoRequest request){
        return ResponseEntity.ok(herramientaService.cambiarEstado(id,request.activa()));
    }

}
