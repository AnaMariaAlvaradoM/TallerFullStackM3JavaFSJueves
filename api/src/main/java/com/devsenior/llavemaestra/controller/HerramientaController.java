package com.devsenior.llavemaestra.controller;

import java.time.LocalDate;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.devsenior.llavemaestra.dto.EstadoRequest;
import com.devsenior.llavemaestra.dto.FranjaOcupada;
import com.devsenior.llavemaestra.dto.HerramientaRequest;
import com.devsenior.llavemaestra.dto.HerramientaResponse;
import com.devsenior.llavemaestra.service.HerramientaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController 
@Tag(name = "Herramientas", description = "Catálogo, ocupación y gestión de herramientas")

@RequestMapping ("/api/herramientas")
public class HerramientaController {

    private final HerramientaService herramientaService;

    public HerramientaController(HerramientaService herramientaService) {
        this.herramientaService = herramientaService;
    } 

    @PreAuthorize("hasAuthority('HERRAMIENTAS_VER')")
    @Operation(summary = "Listar espacios", description = "La administración ve todos; un miembro, solo los activos.")
    @GetMapping
    public ResponseEntity<List<HerramientaResponse>> listar(Authentication autenticacion) {
        return ResponseEntity.ok(herramientaService.listar(autenticacion));
    }

    @PreAuthorize("hasAuthority('HERRAMIENTAS_VER')")
    @Operation(summary = "Ver un espacio")
    @GetMapping("/{id}")
    public ResponseEntity<HerramientaResponse> buscarPorId(@PathVariable Long id, Authentication autenticacion) {
        return ResponseEntity.ok(herramientaService.buscarPorId(id, autenticacion));
    }

    // Horarios ocupados de un espacio en una fecha: /api/espacios/3/ocupacion?fecha=2026-10-20
    @PreAuthorize("hasAuthority('HERRAMIENTAS_VER')")
    @Operation(summary = "Horarios ocupados de un día", description = "Las franjas reservadas de ese espacio en ?fecha=…")
    @GetMapping("/{id}/ocupacion")
    public ResponseEntity<List<FranjaOcupada>> ocupacion(@PathVariable Long id, @RequestParam LocalDate fecha) {
        return ResponseEntity.ok(herramientaService.ocupacion(id, fecha));
    }

    @PreAuthorize("hasAuthority('HERRAMIENTAS_GESTIONAR')")
    @Operation(summary = "Crear un espacio", description = "Entra activo. 409 si el nombre ya existe.")
    @PostMapping
    public ResponseEntity<HerramientaResponse> crear(@Valid @RequestBody HerramientaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(herramientaService.crear(request));
    }

    @PreAuthorize("hasAuthority('HERRAMIENTAS_GESTIONAR')")
    @Operation(summary = "Editar un espacio")
    @PutMapping("/{id}")
    public ResponseEntity<HerramientaResponse> actualizar(@PathVariable Long id,
                                                      @Valid @RequestBody HerramientaRequest request) {
        return ResponseEntity.ok(herramientaService.actualizar(id, request));
    }

    // Activar o desactivar: { "activo": false }
    @PreAuthorize("hasAuthority('HERRAMIENTAS_GESTIONAR')")
    @Operation(summary = "Activar o desactivar na herramienta")
    @PatchMapping("/{id}/estado")
    public ResponseEntity<HerramientaResponse> cambiarEstado(@PathVariable Long id,
                                                         @Valid @RequestBody EstadoRequest request) {
        return ResponseEntity.ok(herramientaService.cambiarEstado(id, request.activa()));
    }

}
