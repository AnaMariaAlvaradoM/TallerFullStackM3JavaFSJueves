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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/herramientas")
@RequiredArgsConstructor
@Tag(name = "Herramientas", description = "Catálogo de herramientas disponibles para alquiler")
public class HerramientaController {

    private final HerramientaService herramientaService;

    @Operation(summary = "Listar herramientas", description = "Retorna el catálogo completo de herramientas")
    @GetMapping
    @PreAuthorize("hasAuthority('HERRAMIENTAS_VER')")
    public ResponseEntity<List<HerramientaResponse>> listarHerramientas() {
        return ResponseEntity.ok(herramientaService.listarHerramientas());
    }

    @Operation(summary = "Buscar herramienta por id", description = "Retorna una herramienta por su identificador único")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('HERRAMIENTAS_VER')")
    public ResponseEntity<HerramientaResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(herramientaService.buscarPorId(id));
    }

    @Operation(summary = "Activar o retirar herramienta", description = "ADMIN: activa o retira una herramienta del catálogo")
    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAuthority('HERRAMIENTAS_GESTIONAR')")
    public ResponseEntity<HerramientaResponse> cambiarEstado(
            @PathVariable Long id,
            @RequestBody EstadoHerramientaRequest request) {
        boolean activa = request.activa() != null && request.activa();
        return ResponseEntity.ok(herramientaService.cambiarEstado(id, activa));
    }

    @Operation(summary = "Crear herramienta", description = "ADMIN: agrega una nueva herramienta al catálogo")
    @PostMapping
    @PreAuthorize("hasAuthority('HERRAMIENTAS_GESTIONAR')")
    public ResponseEntity<HerramientaResponse> crearHerramienta(@RequestBody HerramientaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(herramientaService.crear(request));
    }

    @Operation(summary = "Actualizar herramienta", description = "ADMIN: modifica los datos de una herramienta existente")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('HERRAMIENTAS_GESTIONAR')")
    public ResponseEntity<HerramientaResponse> actualizarHerramienta(
            @PathVariable Long id,
            @RequestBody HerramientaRequest request) {
        return ResponseEntity.ok(herramientaService.actualizar(id, request));
    }
}
