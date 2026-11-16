package com.devsenior.llavemaestra.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.devsenior.llavemaestra.dto.EstadoHerramientaRequest;
import com.devsenior.llavemaestra.dto.HerramientaRequest;
import com.devsenior.llavemaestra.dto.HerramientaResponse;
import com.devsenior.llavemaestra.service.HerramientaService;

import jakarta.validation.Valid;

@RestController 
@RequestMapping ("/api/herramientas")
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

    @PreAuthorize("hasAuthority('HERRAMIENTAS_GESTIONAR')")
    @PostMapping
    public ResponseEntity<HerramientaResponse> crear(@Valid @RequestBody HerramientaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(herramientaService.crear(request));
    }

    @PreAuthorize("hasAuthority('HERRAMIENTAS_GESTIONAR')")
    @PutMapping("/{id}")
    public ResponseEntity<HerramientaResponse> actualizar(@PathVariable Long id,
                                                      @Valid @RequestBody HerramientaRequest request) {
        return ResponseEntity.ok(herramientaService.actualizar(id, request));
    }

    @PreAuthorize("hasAuthority('HERRAMIENTAS_GESTIONAR')")
    @PatchMapping("/{id}/estado")
    public ResponseEntity<HerramientaResponse> cambiarEstado(@PathVariable Long id,
                                                         @Valid @RequestBody EstadoHerramientaRequest request) {
        return ResponseEntity.ok(herramientaService.cambiarEstado(id, request.activa()));
    }
}
