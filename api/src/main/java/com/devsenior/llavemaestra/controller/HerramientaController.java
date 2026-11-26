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
import com.devsenior.llavemaestra.model.Permiso;
import com.devsenior.llavemaestra.service.HerramientaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/herramientas")
@Tag(name = "Herramientas", description = "Catálogo, consulta, creación, edición y retiro de herramientas")
public class HerramientaController {

    private final HerramientaService herramientaService;

    public HerramientaController(HerramientaService herramientaService) {
        this.herramientaService = herramientaService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('HERRAMIENTAS_VER')")
    @Operation(summary = "Listar herramientas", description = "Devuelve todas si tiene HERRAMIENTAS_GESTIONAR; si no, solo activas.")
    public ResponseEntity<List<HerramientaResponse>> listar(Authentication authentication) {
        return ResponseEntity.ok(herramientaService.listar(puedeGestionar(authentication)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('HERRAMIENTAS_VER')")
    @Operation(summary = "Obtener herramienta por ID", description = "Consulta el detalle de una herramienta.")
    public ResponseEntity<HerramientaResponse> obtenerPorId(@PathVariable Long id, Authentication authentication) {
        return ResponseEntity.ok(herramientaService.obtenerPorId(id, puedeGestionar(authentication)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('HERRAMIENTAS_GESTIONAR')")
    @Operation(summary = "Crear herramienta", description = "Registra una nueva herramienta activa en el catálogo.")
    public ResponseEntity<HerramientaResponse> crear(@Valid @RequestBody HerramientaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(herramientaService.crear(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('HERRAMIENTAS_GESTIONAR')")
    @Operation(summary = "Editar herramienta", description = "Actualiza los datos de una herramienta existente.")
    public ResponseEntity<HerramientaResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody HerramientaRequest request
    ) {
        return ResponseEntity.ok(herramientaService.actualizar(id, request));
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAuthority('HERRAMIENTAS_GESTIONAR')")
    @Operation(summary = "Cambiar estado de herramienta", description = "Activa o retira una herramienta.")
    public ResponseEntity<HerramientaResponse> cambiarEstado(
            @PathVariable Long id,
            @Valid @RequestBody EstadoHerramientaRequest request
    ) {
        return ResponseEntity.ok(herramientaService.cambiarEstado(id, request.activa()));
    }

    private boolean puedeGestionar(Authentication authentication) {
        if (authentication == null) {
            return false;
        }
        String permisoGestion = Permiso.HERRAMIENTAS_GESTIONAR.name();
        return authentication.getAuthorities().stream()
                .anyMatch(auth -> permisoGestion.equals(auth.getAuthority()));
    }
}