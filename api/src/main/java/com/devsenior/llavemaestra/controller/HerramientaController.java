package com.devsenior.llavemaestra.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import com.devsenior.llavemaestra.dto.EstadoHerramientaRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.devsenior.llavemaestra.dto.HerramientaResponse;
import com.devsenior.llavemaestra.service.HerramientaService;

import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/herramientas")
public class HerramientaController {
    private final HerramientaService herramientaService;

    public HerramientaController(HerramientaService herramientaService) {
        this.herramientaService = herramientaService;
    }

    @PreAuthorize("hasAuthority('HERRAMIENTAS_VER')")
    @GetMapping
    public ResponseEntity<List<HerramientaResponse>> listar(Authentication autenticacion) {
        return ResponseEntity.ok(herramientaService.listar(autenticacion));
    }

    @PreAuthorize("hasAuthority('HERRAMIENTAS_VER')")
    @GetMapping("/{id}")
    public ResponseEntity<HerramientaResponse> buscarPorId(@PathVariable Long id, Authentication autenticacion) {
        return ResponseEntity.ok(herramientaService.buscarPorId(id, autenticacion));
    }

    // Retirar o volver a ofrecer: { "activa": false }
    @PreAuthorize("hasAuthority('HERRAMIENTAS_GESTIONAR')")
    @PatchMapping("/{id}/estado")
    public ResponseEntity<HerramientaResponse> cambiarEstado(@PathVariable Long id,
                                                         @Valid @RequestBody EstadoHerramientaRequest request) {
        return ResponseEntity.ok(herramientaService.cambiarEstado(id, request.activa()));
    }
}
