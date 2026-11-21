package com.devsenior.llavemaestra.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/herramientas")
@RequiredArgsConstructor
public class HerramientaController {

    private final HerramientaService herramientaService;

    @GetMapping
    @PreAuthorize("hasAuthority('HERRAMIENTAS_VER')")
    public ResponseEntity<List<HerramientaResponse>> listarHerramientas() {
        return ResponseEntity.ok(herramientaService.listarHerramientas());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('HERRAMIENTAS_VER')")
    public ResponseEntity<HerramientaResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(herramientaService.buscarPorId(id));
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAuthority('HERRAMIENTAS_GESTIONAR')")
    public ResponseEntity<HerramientaResponse> cambiarEstado(
            @PathVariable Long id,
            @RequestBody EstadoHerramientaRequest request) {
        boolean activa = request.activa() != null && request.activa();
        return ResponseEntity.ok(herramientaService.cambiarEstado(id, activa));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('HERRAMIENTAS_GESTIONAR')")
    public ResponseEntity<HerramientaResponse> crearHerramienta(@RequestBody HerramientaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(herramientaService.crear(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('HERRAMIENTAS_GESTIONAR')")
    public ResponseEntity<HerramientaResponse> actualizarHerramienta(
            @PathVariable Long id,
            @RequestBody HerramientaRequest request) {
        return ResponseEntity.ok(herramientaService.actualizar(id, request));
    }
}
